package elearningspringboot.controller;

import elearningspringboot.dto.request.PostRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.PostResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.PostService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import elearningspringboot.validation.OnUserCreate;
import elearningspringboot.validation.ValidImageFile;
import jakarta.validation.groups.Default;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService service;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<PageResponse<List<PostResponse>>>> getAllPosts(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {

        log.info("Request: Get posts for management");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long filterAuthorId = null;

        if (AppUtils.hasRole(auth, "TEACHER") && !AppUtils.hasRole(auth, "ADMIN")) {
            filterAuthorId = AppUtils.getUserIdFromSecurityContext();
        }

        PageResponse<List<PostResponse>> response;
        response = service.getAll(pageNumber, pageSize, sorts, keyword, categoryId, false, filterAuthorId);

        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }


    @GetMapping("/public")
    public ResponseEntity<ResponseData<PageResponse<List<PostResponse>>>> getPublicPosts(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {
        PageResponse<List<PostResponse>> response = service.getAll(pageNumber, pageSize, sorts, keyword, categoryId,
                true, null);
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<PostResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get post by id = {}", id);
        PostResponse dto = service.getById(id);
        String message = messageSource.getMessage("post.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<PostResponse>> create(
            @Validated({ OnCreate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile @RequestPart("thumbnail") MultipartFile thumbnail) throws Exception {
        log.info("Request: Create post = {}", request);
        PostResponse dto = service.create(request, thumbnail);
        String message = messageSource.getMessage("post.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData<PostResponse>> createMyPost(
            @Validated({ OnUserCreate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile(message = "{validation.image.file.invalid}") @RequestPart("thumbnail") MultipartFile thumbnail) throws Exception {
        log.info("Request: User create post = {}", request);
        request.setAuthorId(null);
        request.setStatus(null);
        PostResponse dto = service.create(request, thumbnail);
        String message = messageSource.getMessage("post.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<PostResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @Validated({ OnUpdate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile(required = false, message = "{validation.image.file.invalid}") @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail)
            throws Exception {
        log.info("Request: Update post id = {}, data = {}", id, request);
        PostResponse dto = service.update(id, request, thumbnail);
        String message = messageSource.getMessage("post.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PutMapping("/me/{id}")
    public ResponseEntity<ResponseData<PostResponse>> updateMyPost(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @Validated({ OnUserCreate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile(required = false) @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail)
            throws Exception {
        log.info("Request: User update post id = {}, data = {}", id, request);
        request.setAuthorId(null);
        request.setStatus(null);
        PostResponse dto = service.update(id, request, thumbnail);
        String message = messageSource.getMessage("post.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete post id = {}", id);
        service.delete(id);
        String message = messageSource.getMessage("post.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/public/{id}/related")
    public ResponseEntity<ResponseData<List<PostResponse>>> getRelated(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @RequestParam(defaultValue = "6") @Min(value = 1, message = "{validation.page.size.min}") int limit) {
        log.info("Request: Get related posts for id={}, limit={}", id, limit);
        List<PostResponse> data = service.getRelated(id, Math.min(limit, 20));
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, data);
    }

    @GetMapping("/public/latest")
    public ResponseEntity<ResponseData<List<PostResponse>>> getLatestPosts(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int limit) {
        log.info("Request: Get latest posts with limit={}", limit);
        List<PostResponse> data = service.getLatestPosts(Math.min(limit, 50)); // Max 50
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, data);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseData<PageResponse<List<PostResponse>>>> getMyPosts(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {
        log.info("Request: Get my posts with pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber, pageSize,
                sorts, keyword);
        Long authorId = AppUtils.getUserIdFromSecurityContext();
        PageResponse<List<PostResponse>> response;
        response = service.getAll(pageNumber, pageSize, sorts, keyword, categoryId, false, authorId);
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }
}