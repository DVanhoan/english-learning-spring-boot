package elearningspringboot.controller;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.ChapterRequest;
import elearningspringboot.dto.response.ChapterResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.ChapterService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/chapters")
public class ChapterController {

    private final ChapterService service;
    private final MessageSource messageSource;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/by-course/{id}")
    public ResponseEntity<ResponseData<List<ChapterResponse>>> getChaptersByCourseId(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long courseId) {
        log.info("Request: Get chapter by course id={}", courseId);
        List<ChapterResponse> dto = service.getChaptersByCourseId(courseId);
        String message = messageSource.getMessage("chapter.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ChapterResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get chapter id={}", id);
        ChapterResponse dto = service.getById(id);
        String message = messageSource.getMessage("chapter.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<ChapterResponse>> create(
            @Validated({ OnCreate.class, Default.class }) @RequestBody ChapterRequest request) {
        log.info("Request: Create chapter");
        ChapterResponse dto = service.create(request);
        String message = messageSource.getMessage("chapter.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PatchMapping("/order/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<Void>> updateOrder(
            @PathVariable("courseId") @Min(value = 1, message = "{validation.id.min}") Long id,
            @RequestBody List<ChangeOrderRequest> request) {
        log.info("Request: Update order chapter with course id ={}", id);
        service.updateOrder(id, request);
        String message = messageSource.getMessage("chapter.update.order.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<ChapterResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @Validated({ OnUpdate.class, Default.class }) @RequestBody ChapterRequest request) {
        log.info("Request: Update chapter id={}", id);
        ChapterResponse dto = service.update(id, request);
        String message = messageSource.getMessage("chapter.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete chapter id={}", id);
        service.delete(id);
        String message = messageSource.getMessage("chapter.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }
}
