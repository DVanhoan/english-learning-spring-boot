package elearningspringboot.controller;

import elearningspringboot.dto.request.CourseRequest;
import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.enumeration.StatusCourse;
import elearningspringboot.service.CourseService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.ValidImageFile;
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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService service;
    private final MessageSource messageSource;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public ResponseEntity<ResponseData<PageResponse<List<CourseResponse>>>> getAllForAdminAndTeacher(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) StatusCourse status) {
        log.info("Request: Get courses pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber, pageSize, sorts,
                keyword);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isTeacher = AppUtils.hasRole(auth, "TEACHER");
        Long teacherId = null;
        if (isTeacher) {
            teacherId = AppUtils.getUserIdFromSecurityContext();
        }
        PageResponse<List<CourseResponse>> response = service.getAllWithPagination(pageNumber, pageSize, sorts, keyword,
                categoryId, teacherId, status);
        String message = messageSource.getMessage("course.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/public")
    public ResponseEntity<ResponseData<PageResponse<List<CourseResponse>>>> getAllForUsers(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long teacherId) {
        log.info("Request: Get courses pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber, pageSize, sorts,
                keyword);
        PageResponse<List<CourseResponse>> response = service.getAllWithPagination(pageNumber, pageSize, sorts, keyword,
                categoryId,
                teacherId, StatusCourse.PUBLIC);
        String message = messageSource.getMessage("course.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/public/details/{courseId}")
    public ResponseEntity<ResponseData<CourseResponse>> getDetailsCourse(@PathVariable @Min(value = 1, message = "{validation.id.min}") Long courseId) {
        log.info("Request: Get courses details with id = {} ", courseId);
        CourseResponse response = service.getDetailsCourseById(courseId);
        String message = messageSource.getMessage("course.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<CourseResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get course id={}", id);
        CourseResponse response = service.getById(id);
        String message = messageSource.getMessage("course.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseResponse>> create(
            @ValidImageFile(message = "{validation.image.file.invalid}") @RequestPart(value = "thumbnail") MultipartFile thumbnail,
            @Valid @RequestPart("course") CourseRequest request) {
        log.info("Request: Create course={}", request);
        CourseResponse dto = service.create(request, thumbnail);
        String message = messageSource.getMessage("course.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @ValidImageFile(required = false, message = "{validation.image.file.invalid}") @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @Valid @RequestPart("course") CourseRequest request) {
        log.info("Request: Update course id={}, data={}", id, request);
        CourseResponse dto = service.update(id, request, thumbnail);
        String message = messageSource.getMessage("course.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete course id={}", id);
        service.delete(id);
        String message = messageSource.getMessage("course.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/newest")
    public ResponseEntity<ResponseData<List<CourseResponse>>> getNewest(
            @RequestParam(defaultValue = "4") @Min(value = 1, message = "{validation.count.min}") int n) {
        log.info("Request: Get newest courses n={}", n);
        List<CourseResponse> dtoList = service.getNewest(n);
        String message = messageSource.getMessage("course.get.newest.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dtoList);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<ResponseData<List<CourseResponse>>> getCoursesByIds(
            @RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseBuilder.withData(HttpStatus.OK, "OK", List.of());
        }

        List<CourseResponse> response = service.getCoursesByIds(ids);

        String message = "Lấy thông tin các khóa học thành công";
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }
}
