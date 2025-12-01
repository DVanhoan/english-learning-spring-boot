package elearningspringboot.controller;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.LessonRequest;
import elearningspringboot.dto.response.LessonResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.LessonService;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final LessonService service;
    private final MessageSource messageSource;

    @GetMapping("/by-chapter/{chapterId}")
    public ResponseEntity<ResponseData<List<LessonResponse>>> getAll(
            @Min(value = 1, message = "{validation.id.min}") @PathVariable Long chapterId) {
        log.info("Request: Get lessons bv chapterId={}", chapterId);
        List<LessonResponse> response = service.getLessonsByChapterId(chapterId);
        String message = messageSource.getMessage("lesson.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<LessonResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get lesson id={}", id);
        LessonResponse dto = service.getByIdForAdminAndTeacher(id);
        String message = messageSource.getMessage("lesson.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<LessonResponse>> create(
            @RequestPart(value = "documentFile", required = false) MultipartFile attachment,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @Validated({ OnCreate.class, Default.class }) @RequestPart("lesson") LessonRequest request) {
        log.info("Request: Create lesson");
        LessonResponse dto = service.create(request, attachment, video);
        String message = messageSource.getMessage("lesson.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<LessonResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @RequestPart(value = "documentFile", required = false) MultipartFile attachment,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @Validated({ OnUpdate.class, Default.class }) @RequestPart("lesson") LessonRequest request) {
        log.info("Request: Update lesson id={}, data={}", id, request);
        LessonResponse dto = service.update(id, request, attachment, video);
        String message = messageSource.getMessage("lesson.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PatchMapping("/order/{chapterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<Void>> updateOrder(
            @PathVariable("chapterId") @Min(value = 1, message = "{validation.id.min}") Long chapterId,
            @RequestBody List<ChangeOrderRequest> request) {
        log.info("Request: Update order chapter with course id ={}", chapterId);
        service.updateOrder(chapterId, request);
        String message = messageSource.getMessage("lesson.update.order.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete lesson id={}", id);
        service.delete(id);
        String message = messageSource.getMessage("lesson.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }
}
