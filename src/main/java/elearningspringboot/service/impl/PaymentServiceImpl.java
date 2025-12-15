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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import elearningspringboot.entity.TransactionDetail;
import java.util.stream.Collectors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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
        String vnp_TxnRef = vnpayConfig.getRandomNumber(8);

        // 3. Tạo 1 Giao dịch (Transaction) TỔNG
        Transaction transaction = Transaction.builder()
                .amount(totalAmount)
                .paymentGateway("VNPAY")
                .status(TransactionStatus.PENDING)
                .transactionCode(vnp_TxnRef)
                .student(student)
                .build();

        // 4. Tạo nhiều TransactionDetail (Chi tiết Giao dịch)
        List<TransactionDetail> details = courses.stream().map(course ->
                TransactionDetail.builder()
                        .transaction(transaction)
                        .course(course)
                        .price(course.getDiscountPrice())
                        .commissionRate(course.getCommissionRate())
                        .build()
        ).collect(Collectors.toList());

        transaction.setDetails(details); // Gắn chi tiết vào giao dịch tổng
        transactionRepository.save(transaction); // Lưu (sẽ tự động lưu cả details nhờ CascadeType.ALL)

        // 5. Tạo URL VNPAY (với tổng số tiền)
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", vnpayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountVnpay));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan cho " + courses.size() + " khoa hoc");
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnpayConfig.getIpAddress(httpServletRequest));

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
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpayConfig.vnp_PayUrl + "?" + queryUrl;

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
            // 1. Kiểm tra xem giao dịch đã SUCCESS chưa
            if (transactionRepository.existsByTransactionCodeAndStatus(vnp_TxnRef, TransactionStatus.SUCCESS)) {
                log.info("VNPAY IPN: Transaction {} already processed.", vnp_TxnRef);
                return "00"; // Đã xử lý, trả về 00
            }

            // 2. Tìm giao dịch PENDING
            Transaction transaction = transactionRepository.findByTransactionCodeAndStatus(vnp_TxnRef, TransactionStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or already processed: " + vnp_TxnRef));

            // 3. Kiểm tra trạng thái thanh toán
            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                // --- THANH TOÁN THÀNH CÔNG ---
                log.info("VNPAY IPN: Payment success for transaction {}", vnp_TxnRef);
                transaction.setStatus(TransactionStatus.SUCCESS);
                User student = transaction.getStudent();

                for (TransactionDetail detail : transaction.getDetails()) {
                    Course course = detail.getCourse();
                    User teacher = course.getTeacher();
                    double price = detail.getPrice();
                    double commissionRate = detail.getCommissionRate();

                    // 4. Ghi danh (Enroll) cho học viên (cho từng khóa học)
                    if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
                        Enrollment enrollment = Enrollment.builder()
                                .course(course)
                                .student(student)
                                .build();
                        enrollmentRepository.save(enrollment);
                    }

                    // 5. Tính toán chiết khấu và ghi công nợ (cho từng khóa học)
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
                            .ifPresent(cartItem -> {
                                cartItemRepository.delete(cartItem);
                                log.info("Removed course {} from cart for student {}", course.getId(), student.getId());
                            });
                }

                transactionRepository.save(transaction);
                return "00";
            } else {
                log.warn("VNPAY IPN: Payment failed for transaction {}. Code: {}", vnp_TxnRef, vnp_ResponseCode);
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                return "00";
            }
        }catch (Exception e) {
            log.error("VNPAY IPN Error: {}", e.getMessage());
            return "97";
        }
    }
}