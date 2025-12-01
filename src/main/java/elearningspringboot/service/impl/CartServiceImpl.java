package elearningspringboot.service.impl;

import elearningspringboot.dto.request.AddToCartRequest;
import elearningspringboot.dto.response.CartResponse;
import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.dto.response.UserSummaryResponse;
import elearningspringboot.entity.CartItem;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.User;
import elearningspringboot.exception.AppException;
import elearningspringboot.enumeration.ErrorCode;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.CourseMapper;
import elearningspringboot.repository.CartItemRepository;
import elearningspringboot.repository.CourseRepository;
import elearningspringboot.repository.EnrollmentRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.CartService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;
    private final MessageSource messageSource;

    @Override
    public void addToCart(AddToCartRequest request) {
        Long studentId = AppUtils.getUserIdFromSecurityContext();
        Long courseId = request.getCourseId();

        // 1. Kiểm tra xem khóa học có tồn tại không
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale()));
        }

        // 2. Kiểm tra xem đã mua khóa học này chưa
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        // 3. Kiểm tra xem đã có trong giỏ hàng chưa
        if (cartItemRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ALREADY_IN_CART);
        }

        // 4. Lấy thông tin user và course
        User student = userRepository.getReferenceById(studentId);
        Course course = courseRepository.getReferenceById(courseId);

        // 5. Tạo CartItem
        CartItem cartItem = CartItem.builder()
                .student(student)
                .course(course)
                .build();

        cartItemRepository.save(cartItem);
        log.info("Student {} added course {} to cart", studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        Long studentId = AppUtils.getUserIdFromSecurityContext();
        List<CartItem> cartItems = cartItemRepository.findByStudentId(studentId);

        List<CourseResponse> courseResponses = cartItems.stream()
                .map(cartItem -> {
                    Course course = cartItem.getCourse();
                    CourseResponse res = courseMapper.toDTO(course);

                    if (course.getTeacher() != null) {
                        res.setTeacher(UserSummaryResponse.builder()
                                .id(course.getTeacher().getId())
                                .fullName(course.getTeacher().getFullName())
                                .avatarUrl(course.getTeacher().getAvatarUrl())
                                .role(course.getTeacher().getRole().getRole().getName())
                                .build());
                    }
                    return res;
                })
                .collect(Collectors.toList());

        double subtotal = courseResponses.stream().mapToDouble(CourseResponse::getDiscountPrice).sum();
        double totalOriginalPrice = courseResponses.stream().mapToDouble(CourseResponse::getPrice).sum();

        return new CartResponse(courseResponses, courseResponses.size(), subtotal, totalOriginalPrice);
    }

    @Override
    public void removeFromCart(Long courseId) {
        Long studentId = AppUtils.getUserIdFromSecurityContext();

        CartItem cartItem = cartItemRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("cart.item.notFound", null, LocaleContextHolder.getLocale())));

        cartItemRepository.delete(cartItem);
        log.info("Student {} removed course {} from cart", studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount() {
        Long studentId = AppUtils.getUserIdFromSecurityContext();
        return cartItemRepository.findByStudentId(studentId).size();
    }
}