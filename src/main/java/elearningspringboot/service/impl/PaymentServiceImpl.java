package elearningspringboot.service.impl;

import elearningspringboot.configuration.VnpayConfig;
import elearningspringboot.dto.request.CreatePaymentRequest;
import elearningspringboot.dto.response.CreatePaymentResponse;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.Enrollment;
import elearningspringboot.entity.TeacherPayout;
import elearningspringboot.entity.Transaction;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.PayoutStatus;
import elearningspringboot.enumeration.TransactionStatus;
import elearningspringboot.exception.AppException;
import elearningspringboot.enumeration.ErrorCode;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.repository.*;
import elearningspringboot.service.PaymentService;
import elearningspringboot.util.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import elearningspringboot.entity.TransactionDetail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TransactionRepository transactionRepository;
    private final TeacherPayoutRepository teacherPayoutRepository;
    private final CartItemRepository cartItemRepository;

    private final VnpayConfig vnpayConfig;

    @Override
    @Transactional
    public CreatePaymentResponse createVnPayPayment(CreatePaymentRequest request, HttpServletRequest httpServletRequest)
            throws UnsupportedEncodingException {

        Long studentId = AppUtils.getUserIdFromSecurityContext();
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // 1. Lấy danh sách khóa học và tính tổng tiền
        List<Course> courses = courseRepository.findAllById(request.getCourseIds());
        if (courses.size() != request.getCourseIds().size()) {
            throw new ResourceNotFoundException("Một vài khóa học không tìm thấy.");
        }

        double totalAmount = 0;
        for (Course course : courses) {
            // 2. Kiểm tra xem đã mua chưa
            if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, course.getId())) {
                throw new AppException(ErrorCode.ALREADY_ENROLLED);
            }
            totalAmount += course.getDiscountPrice();
        }

        long amountVnpay = (long) (totalAmount * 100);

        // Gọi hàm static từ Class VnpayConfig
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);

        // 3. Tạo Transaction
        Transaction transaction = Transaction.builder()
                .amount(totalAmount)
                .paymentGateway("VNPAY")
                .status(TransactionStatus.PENDING)
                .transactionCode(vnp_TxnRef)
                .student(student)
                .build();

        // 4. Tạo Details
        List<TransactionDetail> details = courses.stream().map(course ->
                TransactionDetail.builder()
                        .transaction(transaction)
                        .course(course)
                        .price(course.getDiscountPrice())
                        .commissionRate(course.getCommissionRate())
                        .build()
        ).collect(Collectors.toList());

        transaction.setDetails(details);
        transactionRepository.save(transaction);

        // 5. Tạo URL VNPAY
        Map<String, String> vnp_Params = new HashMap<>();
        // Dùng hằng số static
        vnp_Params.put("vnp_Version", VnpayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnpayConfig.vnp_Command);

        vnp_Params.put("vnp_TmnCode", vnpayConfig.getVnp_TmnCode());

        vnp_Params.put("vnp_Amount", String.valueOf(amountVnpay));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan cho " + courses.size() + " khoa hoc");
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");

        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnp_ReturnUrl());

        vnp_Params.put("vnp_IpAddr", VnpayConfig.getIpAddress(httpServletRequest));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query url
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();

        String vnp_SecureHash = VnpayConfig.hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return new CreatePaymentResponse(paymentUrl);
    }

    @Override
    @Transactional
    public String handleVnPayIPN(Map<String, String> vnpayParams) {
        log.info("VNPAY IPN Received: {}", vnpayParams);
        String vnp_TxnRef = vnpayParams.get("vnp_TxnRef");
        String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");
        String vnp_TransactionStatus = vnpayParams.get("vnp_TransactionStatus");
        try {
            if (transactionRepository.existsByTransactionCodeAndStatus(vnp_TxnRef, TransactionStatus.SUCCESS)) {
                return "00";
            }

            Transaction transaction = transactionRepository.findByTransactionCodeAndStatus(vnp_TxnRef, TransactionStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + vnp_TxnRef));

            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                User student = transaction.getStudent();

                for (TransactionDetail detail : transaction.getDetails()) {
                    Course course = detail.getCourse();
                    User teacher = course.getTeacher();
                    double price = detail.getPrice();
                    double commissionRate = detail.getCommissionRate();

                    if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
                        Enrollment enrollment = Enrollment.builder()
                                .course(course)
                                .student(student)
                                .build();
                        enrollmentRepository.save(enrollment);
                    }

                    double platformFee = price * commissionRate;
                    double teacherEarning = price - platformFee;

                    TeacherPayout payout = TeacherPayout.builder()
                            .teacher(teacher)
                            .transactionDetail(detail)
                            .amountEarned(teacherEarning)
                            .platformFee(platformFee)
                            .status(PayoutStatus.UNPAID)
                            .build();
                    teacherPayoutRepository.save(payout);

                    cartItemRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                            .ifPresent(cartItemRepository::delete);
                }

                transactionRepository.save(transaction);
                return "00";
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                return "00";
            }
        } catch (Exception e) {
            log.error("VNPAY IPN Error: {}", e.getMessage());
            return "97";
        }
    }
}