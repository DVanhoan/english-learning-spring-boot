package elearningspringboot.service.impl;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.ChapterRequest;
import elearningspringboot.dto.response.ChapterResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.entity.Chapter;
import elearningspringboot.entity.Course;
import elearningspringboot.entity.Lesson;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.ChapterMapper;
import elearningspringboot.repository.ChapterRepository;
import elearningspringboot.repository.CourseRepository;
import elearningspringboot.service.ChapterService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository repository;
    private final CourseRepository courseRepository;
    private final ChapterMapper mapper;
    private final MessageSource messageSource;
    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional
    public ChapterResponse create(ChapterRequest request) {
        Chapter entity = mapper.toEntity(request);
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByCourse(course);
        entity.setCourse(course);
        entity.setOrderIndex(chapterRepository.getOrderIndexMax(request.getCourseId()) + 1);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public ChapterResponse update(Long id, ChapterRequest request) {
        Chapter entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(entity);
        mapper.updateEntityFromRequest(request, entity);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Chapter entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(entity);
        repository.delete(entity);
    }

    @Override
    public ChapterResponse getById(Long id) {
        Chapter entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public List<ChapterResponse> getChaptersByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByCourse(course);
        List<Chapter> chapters = chapterRepository.searchByCourseId(courseId);
        return chapters.stream().map(c -> {
            ChapterResponse chapterResponse = mapper.toDTO(c);
            chapterResponse.setNumberOfLessons(c.getLessons().size());
            chapterResponse.setDuration(
                    c.getLessons().stream()
                            .mapToInt(Lesson::getDuration)
                            .sum()
            );
            return chapterResponse;
        }).toList();
    }

    @Override
    public void updateOrder(Long courseId, List<ChangeOrderRequest> request) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("course.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByCourse(course);
        request.forEach((entity) -> {
            Chapter chapter = repository.findById(entity.getId()).orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
            chapter.setOrderIndex(entity.getOrderIndex());
            repository.save(chapter);
        });
    }


}
