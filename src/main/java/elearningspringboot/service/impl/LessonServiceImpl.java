package elearningspringboot.service.impl;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.LessonRequest;
import elearningspringboot.dto.response.LessonResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.entity.Chapter;
import elearningspringboot.entity.Lesson;
import elearningspringboot.enumeration.StatusCourse;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.LessonMapper;
import elearningspringboot.repository.ChapterRepository;
import elearningspringboot.repository.LessonRepository;
import elearningspringboot.service.StorageService;
import elearningspringboot.service.LessonService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.HtmlImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository repository;
    private final ChapterRepository chapterRepository;
    private final LessonMapper mapper;
    private final MessageSource messageSource;
    private final StorageService storageService;
    private final HtmlImageProcessor htmlImageProcessor;

    @Override
    @Transactional
    public LessonResponse create(LessonRequest request, MultipartFile attachment, MultipartFile video) {
        Lesson entity = mapper.toEntity(request);
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);
        entity.setChapter(chapter);

        if (attachment != null && !attachment.isEmpty()) {
            String attachmentUrl = storageService.uploadRawFile(attachment);
            entity.setAttachmentUrl(attachmentUrl);
        }
        if (video != null && !video.isEmpty()) {
            String videoUrl = storageService.uploadVideo(video);
            entity.setVideoUrl(videoUrl);
        } else {
            entity.setIsPreview(false);
        }

        if (request.getDescription() != null) {
            String htmlDescription = htmlImageProcessor.process(request.getDescription());
            entity.setDescription(htmlDescription);
        }
        entity.setContent(htmlImageProcessor.process(request.getContent()));

        entity.setOrderIndex(repository.getOrderIndexMax(request.getChapterId()) + 1);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public void updateOrder(Long chapterId, List<ChangeOrderRequest> request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);
        request.forEach((entity) -> {
            Lesson lesson = repository.findById(entity.getId()).orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("lesson.notFound", null, LocaleContextHolder.getLocale())));
            lesson.setOrderIndex(entity.getOrderIndex());
            repository.save(lesson);
        });
    }

    @Override
    @Transactional
    public LessonResponse update(Long id, LessonRequest request, MultipartFile attachment, MultipartFile video) {
        Lesson entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("lesson.notFound", null, LocaleContextHolder.getLocale())));
        mapper.updateEntityFromRequest(request, entity);
        Chapter chapter = chapterRepository.findById(entity.getChapter().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);

        if (attachment != null && !attachment.isEmpty()) {
            String attachmentUrl = storageService.uploadRawFile(attachment);
            entity.setAttachmentUrl(attachmentUrl);
        }
        if (video != null && !video.isEmpty()) {
            String videoUrl = storageService.uploadVideo(video);
            entity.setVideoUrl(videoUrl);
        }

        if (request.getDescription() != null) {
            String htmlDescription = htmlImageProcessor.process(request.getDescription());
            entity.setDescription(htmlDescription);
        }
        entity.setContent(htmlImageProcessor.process(request.getContent()));

        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Lesson entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("lesson.notFound", null, LocaleContextHolder.getLocale())));
        Chapter chapter = chapterRepository.findById(entity.getChapter().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);
        repository.delete(entity);
    }

    @Override
    public LessonResponse getByIdForAdminAndTeacher(Long id) {
        Lesson entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("lesson.notFound", null, LocaleContextHolder.getLocale())));
        Chapter chapter = chapterRepository.findById(entity.getChapter().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);
        return mapper.toDTO(entity);
    }

    @Override
    public List<LessonResponse> getLessonsByChapterId(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("chapter.notFound", null, LocaleContextHolder.getLocale())));
        AppUtils.checkOwnerByChapter(chapter);
        List<Lesson> lessons = repository.findByChapterId(chapterId);
        return mapper.toDTOs(lessons);
    }
}
