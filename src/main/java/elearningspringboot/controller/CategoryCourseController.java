package elearningspringboot.controller;

import elearningspringboot.dto.request.CategoryCourseRequest;
import elearningspringboot.dto.response.CategoryCourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.CategoryCourseService;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import jakarta.validation.groups.Default;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/v1/categories-course")
public class CategoryCourseController {

        private final CategoryCourseService service;
        private final MessageSource messageSource;

        @GetMapping
        public ResponseEntity<ResponseData<PageResponse<List<CategoryCourseResponse>>>> getAll(
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
                        @RequestParam(required = false) List<String> sorts,
                        @RequestParam(defaultValue = "") String keyword) {
                log.info("Request: Get categories with pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber,
                                pageSize,
                                sorts, keyword);
                PageResponse<List<CategoryCourseResponse>> response = service.getCategoriesWithPaginationAndKeyword(
                                pageNumber,
                                pageSize, sorts, keyword);
                String message = messageSource.getMessage("categoryCourse.get.list.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseData<CategoryCourseResponse>> getById(
                        @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
                log.info("Request: Get category course by id = {}", id);
                CategoryCourseResponse dto = service.getById(id);
                String message = messageSource.getMessage("categoryCourse.get.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, dto);
        }

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseData<CategoryCourseResponse>> create(
                        @Validated({ OnCreate.class, Default.class }) @RequestBody CategoryCourseRequest request) {
                log.info("Request: Create category course = {}", request);
                CategoryCourseResponse dto = service.create(request);
                String message = messageSource.getMessage("categoryCourse.create.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseData<CategoryCourseResponse>> update(
                        @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
                        @Validated({ OnUpdate.class, Default.class }) @RequestBody CategoryCourseRequest request) {
                log.info("Request: Update category course id = {}, data = {}", id, request);
                CategoryCourseResponse dto = service.update(id, request);
                String message = messageSource.getMessage("categoryCourse.update.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, dto);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseData<Void>> delete(
                        @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
                log.info("Request: Delete category course id = {}", id);
                service.delete(id);
                String message = messageSource.getMessage("categoryCourse.delete.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.noData(HttpStatus.OK, message);
        }
}
