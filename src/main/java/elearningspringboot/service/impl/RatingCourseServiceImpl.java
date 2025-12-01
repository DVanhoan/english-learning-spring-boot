package elearningspringboot.service.impl;

import elearningspringboot.dto.request.RatingRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.RatingResponse;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.RatingCourse;
import elearningspringboot.entity.User;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.RatingCourseMapper;
import elearningspringboot.repository.CourseRepository;
import elearningspringboot.repository.RatingCourseRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.RatingCourseService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RatingCourseServiceImpl implements RatingCourseService {

    private final RatingCourseRepository ratingRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RatingCourseMapper ratingMapper;
    private final MessageSource messageSource;

    @Override
    public RatingResponse createOrUpdateRating(RatingRequest request) {
        Long authorId = AppUtils.getUserIdFromSecurityContext();

        // 1. Lấy thông tin Course và Author
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // TODO: Thêm logic kiểm tra xem người dùng đã mua/tham gia khóa học này chưa
        // if (!enrollmentRepository.existsByCourseIdAndUserId(course.getId(), author.getId())) {
        //    throw new AccessDeniedException("Bạn phải tham gia khóa học trước khi đánh giá.");
        // }

        // 2. Kiểm tra xem người dùng đã đánh giá khóa này bao giờ chưa
        Optional<RatingCourse> existingRatingOpt = ratingRepository.findByCourseIdAndAuthorId(course.getId(), author.getId());

        if (existingRatingOpt.isPresent()) {
            // 3a. Nếu đã tồn tại -> Cập nhật (Update)
            log.info("Updating existing rating for courseId: {}, userId: {}", course.getId(), author.getId());
            RatingCourse existingRating = existingRatingOpt.get();
            ratingMapper.updateEntityFromRequest(request, existingRating); // Chỉ cập nhật rating và message
            RatingCourse updatedRating = ratingRepository.save(existingRating);
            return ratingMapper.toDTO(updatedRating);
        } else {
            // 3b. Nếu chưa tồn tại -> Tạo mới (Create)
            log.info("Creating new rating for courseId: {}, userId: {}", course.getId(), author.getId());
            RatingCourse newRating = ratingMapper.toEntity(request);
            newRating.setCourse(course);
            newRating.setAuthor(author);
            RatingCourse savedRating = ratingRepository.save(newRating);
            return ratingMapper.toDTO(savedRating);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<RatingResponse>> getRatingsByCourse(Long courseId, int pageNumber, int pageSize) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale()));
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("createdAt").descending());
        Page<RatingCourse> page = ratingRepository.findByCourseId(courseId, pageable);

        List<RatingResponse> dtos = page.getContent().stream()
                .map(ratingMapper::toDTO)
                .toList();

        return PageResponse.<List<RatingResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(dtos)
                .build();
    }

    @Override
    public void deleteRating(Long ratingId) {

        RatingCourse rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("rating.notFound", null, LocaleContextHolder.getLocale())));

        log.info("Deleting ratingId: {}", ratingId);
        ratingRepository.delete(rating);
    }
}