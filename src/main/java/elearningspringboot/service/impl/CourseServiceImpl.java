package elearningspringboot.service.impl;

import elearningspringboot.dto.request.CourseRequest;
import elearningspringboot.dto.response.*;
import elearningspringboot.entity.CategoryCourse;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.Lesson;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.StatusCourse;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.ChapterMapper;
import elearningspringboot.mapper.CourseMapper;
import elearningspringboot.mapper.LessonMapper;
import elearningspringboot.repository.CategoryCourseRepository;
import elearningspringboot.repository.CourseRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.StorageService;
import elearningspringboot.service.CourseService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final CategoryCourseRepository categoryRepository;
    private final UserRepository userRepository;
    private final CourseMapper mapper;
    private final StorageService storageService;
    private final MessageSource messageSource;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public CourseResponse create(CourseRequest request, MultipartFile thumbnail) {
        CategoryCourse category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("categoryCourse.notFound", null, LocaleContextHolder.getLocale())));
        User teacher = userRepository.findTeacherById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("teacher.not.found.by.id",
                        new Object[] { request.getTeacherId() }, LocaleContextHolder.getLocale())));
        Course entity = mapper.toEntity(request);
        if (request.getStatus() != null) {
            entity.setStatus(StatusCourse.fromValue(request.getStatus()));
        }
        entity.setCategory(category);
        entity.setTeacher(teacher);
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(thumbnailUrl);
        }
        repository.save(entity);
        CourseResponse res = mapper.toDTO(entity);
        res.setTeacher(UserSummaryResponse.builder()
                .id(teacher.getId()).fullName(teacher.getFullName()).avatarUrl(teacher.getAvatarUrl())
                .role(teacher.getRole().getRole().getName())
                .build());
        return res;
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseRequest request, MultipartFile thumbnail) {
        Course entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        mapper.updateEntityFromRequest(request, entity);
        if (request.getStatus() != null) {
            entity.setStatus(StatusCourse.fromValue(request.getStatus()));
        }
        if (request.getCategoryId() != null) {
            CategoryCourse category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageSource.getMessage("categoryCourse.notFound", null,
                                    LocaleContextHolder.getLocale())));
            entity.setCategory(category);
        }
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findTeacherById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("teacher.not.found.by.id",
                            new Object[] { request.getTeacherId() }, LocaleContextHolder.getLocale())));
            entity.setTeacher(teacher);
        }
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(thumbnailUrl);
        }
        repository.save(entity);
        CourseResponse res = mapper.toDTO(entity);
        res.setTeacher(UserSummaryResponse.builder()
                .id(entity.getTeacher().getId()).fullName(entity.getTeacher().getFullName())
                .avatarUrl(entity.getTeacher().getAvatarUrl())
                .role(entity.getTeacher().getRole().getRole().getName())
                .build());
        return res;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        repository.delete(entity);
    }

    @PostAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and returnObject.teacher.id == authentication.principal.id)")
    @Override
    public CourseResponse getById(Long id) {
        Course entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        CourseResponse res = mapper.toDTO(entity);
        res.setTeacher(UserSummaryResponse.builder()
                .id(entity.getTeacher().getId()).fullName(entity.getTeacher().getFullName())
                .avatarUrl(entity.getTeacher().getAvatarUrl())
                .role(entity.getTeacher().getRole().getRole().getName())
                .build());
        res.setNumberOfLessons(getNumberOfLessons(entity));
        return res;
    }

    @PostAuthorize("returnObject.status == T(elearningspringboot.enumeration.StatusCourse).PUBLIC or hasRole('ADMIN') or (hasRole('TEACHER') and returnObject.teacher.id == authentication.principal.id)")
    @Override
    public CourseResponse getDetailsCourseById (Long id) {
        Course entity = repository.findByIdWithChaptersAndLessons(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        CourseResponse res = mapper.toDTO(entity);
        res.setTeacher(UserSummaryResponse.builder()
                .id(entity.getTeacher().getId()).fullName(entity.getTeacher().getFullName())
                .avatarUrl(entity.getTeacher().getAvatarUrl())
                .role(entity.getTeacher().getRole().getRole().getName())
                .build());
        List<ChapterResponse> chaptersDetails = entity.getChapters().stream().map(chapter -> {
            ChapterResponse chapterResponse = chapterMapper.toDTO(chapter);
            chapterResponse.setNumberOfLessons(chapter.getLessons().size());
            chapterResponse.setDuration(
                    chapter.getLessons().stream()
                            .mapToInt(Lesson::getDuration)
                            .sum()
            );
            chapterResponse.setLessonsDetails(chapter.getLessons().stream().map(lesson -> {;
                LessonResponse lessonResponse = lessonMapper.toDTO(lesson);
                if (!lessonResponse.getIsPreview()) {
                    lessonResponse.setVideoUrl(null);
                    lessonResponse.setAttachmentUrl(null);
                }
                return lessonResponse;
            }).collect(Collectors.toList()));
            return chapterResponse;
        }).toList();
        res.setChaptersDetails(chaptersDetails);
        res.setNumberOfLessons(getNumberOfLessons(entity));
        return res;
    }

    @Override
    public PageResponse<List<CourseResponse>> getAllWithPagination(int pageNumber, int pageSize, List<String> sorts,
            String keyword, Long categoryId, Long teacherId, StatusCourse status) {
        String kw = keyword == null ? "" : keyword.toLowerCase();
        List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt", "title", "price", "discountPrice");
        Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
        Page<Course> page = repository.searchCourses(kw, categoryId, teacherId, status, pageable);
        List<CourseResponse> items = page.getContent().stream().map(entity -> {
            CourseResponse res = mapper.toDTO(entity);
            res.setTeacher(UserSummaryResponse.builder()
                    .id(entity.getTeacher().getId()).fullName(entity.getTeacher().getFullName())
                    .avatarUrl(entity.getTeacher().getAvatarUrl())
                    .role(entity.getTeacher().getRole().getRole().getName())
                    .build());
            res.setNumberOfLessons(entity.getChapters().stream().mapToInt(c -> c.getLessons().size()).sum());
            res.setDuration(entity.getChapters().stream()
                    .mapToInt(c -> c.getLessons().stream().mapToInt(Lesson::getDuration).sum()).sum());
            return res;
        }).toList();
        return PageResponse.<List<CourseResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    public List<CourseResponse> getNewest(int n) {
        int size = Math.max(1, n);
        PageRequest pageable = PageRequest.of(0, size);
        List<Course> courses = repository.findByStatusOrderByCreatedAtDesc(StatusCourse.PUBLIC, pageable);
        return courses.stream().map(entity -> {
            CourseResponse res = mapper.toDTO(entity);
            res.setTeacher(UserSummaryResponse.builder()
                    .id(entity.getTeacher().getId()).fullName(entity.getTeacher().getFullName())
                    .avatarUrl(entity.getTeacher().getAvatarUrl())
                    .role(entity.getTeacher().getRole().getRole().getName())
                    .build());
            res.setNumberOfLessons(entity.getChapters().stream().mapToInt(c -> c.getLessons().size()).sum());
            res.setDuration(entity.getChapters().stream()
                    .mapToInt(c -> c.getLessons().stream().mapToInt(Lesson::getDuration).sum()).sum());
            return res;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByIds(List<Long> ids) {
        // 1. Dùng hàm có sẵn của JpaRepository để lấy danh sách theo ID
        List<Course> courses = repository.findAllById(ids);

        // 2. Map từ Entity sang DTO và bổ sung thông tin thiếu (Teacher, Stats)
        return courses.stream().map(entity -> {
            CourseResponse res = mapper.toDTO(entity);

            // Map Teacher (Giống như trong getNewest hay getAll)
            if (entity.getTeacher() != null) {
                res.setTeacher(UserSummaryResponse.builder()
                        .id(entity.getTeacher().getId())
                        .fullName(entity.getTeacher().getFullName())
                        .avatarUrl(entity.getTeacher().getAvatarUrl())
                        .role(entity.getTeacher().getRole().getRole().getName())
                        .build());
            }

            // Tính tổng số bài học (Dùng hàm helper có sẵn bên dưới)
            res.setNumberOfLessons(getNumberOfLessons(entity));

            // Tính tổng thời lượng
            int totalDuration = entity.getChapters().stream()
                    .mapToInt(c -> c.getLessons().stream()
                            .mapToInt(Lesson::getDuration) // Lấy duration của từng bài
                            .sum())
                    .sum();
            res.setDuration(totalDuration);

            return res;
        }).collect(Collectors.toList());
    }

    private Integer getNumberOfLessons(Course course) {
        return course.getChapters().stream()
                .mapToInt(chapter -> chapter.getLessons().size())
                .sum();
    }
}
