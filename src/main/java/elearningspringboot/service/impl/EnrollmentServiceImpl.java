package elearningspringboot.service.impl;

import elearningspringboot.dto.response.*;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.Enrollment;
import elearningspringboot.entity.User;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.CourseMapper;
import elearningspringboot.repository.CourseRepository;
import elearningspringboot.repository.EnrollmentRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.EnrollmentService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

        private final EnrollmentRepository enrollmentRepository;
        private final CourseRepository courseRepository;
        private final UserRepository userRepository;
        private final CourseMapper mapper;
        private final MessageSource messageSource;

        @Override
        @Transactional
        public CourseResponse enroll(Long courseId, Long studentId) {
                // --- VIẾT LẠI HÀM NÀY CHO GỌN ---

                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));

                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                messageSource.getMessage("user.notFound", new Object[] { studentId }, LocaleContextHolder.getLocale())));

                // Kiểm tra xem đã tồn tại chưa
                if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
                        throw new IllegalStateException(
                                messageSource.getMessage("enrollment.already.enrolled", null,
                                        LocaleContextHolder.getLocale()));
                }

                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollmentRepository.save(enrollment);

                return mapper.toDTO(course);
        }

        @Override
        @Transactional
        public void unEnrollCourse(Long courseId, Long studentId) {
                Enrollment e = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("enrollment.not.found", null,
                                                                LocaleContextHolder.getLocale())));
                enrollmentRepository.delete(e);
        }

        @Override
        public List<CourseResponse> getEnrollmentsForStudent(Long studentId) {
                List<Enrollment> list = enrollmentRepository.findByStudentId(studentId);
                return list.stream().map(en -> {
                        Course c = en.getCourse();
                        CourseResponse res = mapper.toDTO(c);
                        res.setTeacher(UserSummaryResponse.builder()
                                        .id(c.getTeacher().getId()).fullName(c.getTeacher().getFullName())
                                        .avatarUrl(c.getTeacher().getAvatarUrl())
                                        .role(c.getTeacher().getRole().getRole().getName())
                                        .build());
                        res.setNumberOfLessons(c.getChapters().stream().mapToInt(ch -> ch.getLessons().size()).sum());
                        res.setDuration(c.getChapters().stream()
                                        .mapToInt(ch -> ch.getLessons().stream().mapToInt(l -> l.getDuration()).sum())
                                        .sum());
                        return res;
                }).collect(Collectors.toList());
        }

        @Override
        public PageResponse<List<CourseResponse>> getEnrollmentsForStudent(Long studentId, int pageNumber, int pageSize,
                        List<String> sorts, String keyword, Long categoryId) {
                String kw = keyword == null ? "" : keyword.toLowerCase();
                List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt", "title", "price", "discountPrice");
                Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
                Page<Course> page = enrollmentRepository.findCoursesByStudentIdAndKeyword(studentId, kw, categoryId,
                                pageable);
                List<CourseResponse> items = page.getContent().stream().map(entity -> {
                        CourseResponse res = mapper.toDTO(entity);
                        res.setTeacher(UserSummaryResponse.builder()
                                        .id(entity.getTeacher().getId())
                                        .fullName(entity.getTeacher().getFullName())
                                        .avatarUrl(entity.getTeacher().getAvatarUrl())
                                        .role(entity.getTeacher().getRole().getRole().getName())
                                        .build());
                        res.setNumberOfLessons(
                                        entity.getChapters().stream().mapToInt(ch -> ch.getLessons().size()).sum());
                        res.setDuration(entity.getChapters().stream()
                                        .mapToInt(ch -> ch.getLessons().stream().mapToInt(l -> l.getDuration()).sum())
                                        .sum());
                        res.setFirstLessonId(entity.getChapters().stream()
                                        .flatMap(ch -> ch.getLessons().stream())
                                        .sorted((l1, l2) -> l1.getCreatedAt().compareTo(l2.getCreatedAt()))
                                        .map(l -> l.getId())
                                        .findFirst()
                                        .orElse(null));
                        return res;
                }).collect(Collectors.toList());

                return PageResponse.<List<CourseResponse>>builder()
                                .pageNumber(pageNumber)
                                .pageSize(pageSize)
                                .totalPages(page.getTotalPages())
                                .numberOfElements(page.getNumberOfElements())
                                .items(items)
                                .build();
        }

        @Override
        public PageResponse<List<UserSummaryResponse>> getStudentsByCourse(Long courseId, int pageNumber, int pageSize,
                        List<String> sorts, String keyword) {
                // For student sorting, allow fullName, email, createdAt
                String kw = keyword == null ? "" : keyword.toLowerCase();
                List<String> whiteListFieldSorts = List.of("fullName", "email", "createdAt");
                Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
                Page<User> page = enrollmentRepository.findStudentsByCourseIdAndKeyword(courseId, kw, pageable);
                List<UserSummaryResponse> items = page.getContent().stream().map(user -> {
                        return UserSummaryResponse.builder()
                                        .id(user.getId())
                                        .fullName(user.getFullName())
                                        .avatarUrl(user.getAvatarUrl())
                                        .role(user.getRole().getRole().getName())
                                        .build();
                }).collect(Collectors.toList());

                return PageResponse.<List<UserSummaryResponse>>builder()
                                .pageNumber(pageNumber)
                                .pageSize(pageSize)
                                .totalPages(page.getTotalPages())
                                .numberOfElements(page.getNumberOfElements())
                                .items(items)
                                .build();
        }
}
