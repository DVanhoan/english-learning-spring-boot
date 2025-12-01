package elearningspringboot.controller;

import elearningspringboot.dto.request.DictationLessonRequest;
import elearningspringboot.dto.request.DictationTopicRequest;
import elearningspringboot.dto.response.DictationLessonResponse;
import elearningspringboot.dto.response.DictationTopicResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.DictationService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.ResponseBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/dictation")
@RequiredArgsConstructor
@Validated
public class DictationController {

    private final DictationService service;
    private final MessageSource messageSource;

    @GetMapping("/topics")
    public ResponseEntity<ResponseData<List<DictationTopicResponse>>> getAllTopics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (AppUtils.isAuthenticated(auth) && AppUtils.hasRole(auth, "TEACHER") && !AppUtils.hasRole(auth, "ADMIN")) {
            Long teacherId = AppUtils.getUserIdFromSecurityContext();
            return ResponseBuilder.withData(HttpStatus.OK, "Success", service.getTopicsByAuthor(teacherId));
        }

        return ResponseBuilder.withData(HttpStatus.OK, "Success", service.getAllTopics());
    }



    @GetMapping("/topics/{id}")
    public ResponseEntity<ResponseData<DictationTopicResponse>> getTopicById(
            @PathVariable @Min(1) Long id) {
        log.info("Request: Get dictation topic id={}", id);
        DictationTopicResponse response = service.getTopicById(id);
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy chi tiết chủ đề thành công", response);
    }

    @GetMapping("/topics/{id}/lessons")
    public ResponseEntity<ResponseData<List<DictationLessonResponse>>> getLessonsByTopic(
            @PathVariable @Min(1) Long id) {
        log.info("Request: Get lessons by topic id={}", id);
        List<DictationLessonResponse> response = service.getLessonsByTopicId(id);
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy danh sách bài học thành công", response);
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<ResponseData<DictationLessonResponse>> getLessonById(
            @PathVariable @Min(1) Long id) {
        log.info("Request: Get dictation lesson id={}", id);
        DictationLessonResponse response = service.getLessonById(id);
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy chi tiết bài học thành công", response);
    }

    @PostMapping(value = "/topics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<DictationTopicResponse>> createTopic(
            @RequestPart("data") @Valid DictationTopicRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        log.info("Request: Create dictation topic: {}", request.getTitle());
        DictationTopicResponse response = service.createTopic(request, thumbnail);
        String message = messageSource.getMessage("dictation.topic.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
    }

    @PutMapping(value = "/topics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<DictationTopicResponse>> updateTopic(
            @PathVariable @Min(1) Long id,
            @RequestPart("data") @Valid DictationTopicRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        log.info("Request: Update dictation topic id={}", id);
        DictationTopicResponse response = service.updateTopic(id, request, thumbnail);
        String message = messageSource.getMessage("dictation.topic.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @DeleteMapping("/topics/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteTopic(@PathVariable @Min(1) Long id) {
        log.info("Request: Delete dictation topic id={}", id);
        service.deleteTopic(id);
        String message = messageSource.getMessage("dictation.topic.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }


    @PostMapping(value = "/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<DictationLessonResponse>> createLesson(
            @RequestPart("data") @Valid DictationLessonRequest request,
            @RequestPart(value = "audio", required = false) MultipartFile audio) {
        log.info("Request: Create dictation lesson: {}", request.getTitle());
        DictationLessonResponse response = service.createLesson(request, audio);
        String message = messageSource.getMessage("dictation.lesson.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
    }

    @PutMapping(value = "/lessons/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<DictationLessonResponse>> updateLesson(
            @PathVariable @Min(1) Long id,
            @RequestPart("data") @Valid DictationLessonRequest request,
            @RequestPart(value = "audio", required = false) MultipartFile audio) {
        log.info("Request: Update dictation lesson id={}", id);
        DictationLessonResponse response = service.updateLesson(id, request, audio);
        String message = messageSource.getMessage("dictation.lesson.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @DeleteMapping("/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteLesson(@PathVariable @Min(1) Long id) {
        log.info("Request: Delete dictation lesson id={}", id);
        service.deleteLesson(id);
        String message = messageSource.getMessage("dictation.lesson.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }
}