package elearningspringboot.controller;

import elearningspringboot.dto.request.FlashcardSetRequest;
import elearningspringboot.dto.response.FlashcardSetResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.FlashcardService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.ResponseBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService service;

    @GetMapping
    public ResponseEntity<ResponseData<PageResponse<List<FlashcardSetResponse>>>> getAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        var result = service.getAll(page, size, keyword, category, sort);
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy danh sách thành công", result);
    }

    @GetMapping("/management")
    @PostAuthorize("hasRole('ADMIN') or (hasRole('TEACHER'))")
    public ResponseEntity<ResponseData<PageResponse<List<FlashcardSetResponse>>>> getAllForManagement(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long filterAuthorId = null;

        if (AppUtils.hasRole(auth, "TEACHER") && !AppUtils.hasRole(auth, "ADMIN")) {
            filterAuthorId = AppUtils.getUserIdFromSecurityContext();
        }

        var result = service.getAllForManagement(page, size, keyword, category, sort, filterAuthorId);

        return ResponseBuilder.withData(HttpStatus.OK, "Lấy danh sách quản lý thành công", result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<FlashcardSetResponse>> getById(@PathVariable Long id) {
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy chi tiết thành công", service.getById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseData<FlashcardSetResponse>> create(
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart("data") @Valid FlashcardSetRequest request
    ) {
        return ResponseBuilder.withData(HttpStatus.CREATED, "Tạo bộ thẻ thành công", service.create(request, thumbnail));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseData<FlashcardSetResponse>> update(
            @PathVariable Long id,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart("data") @Valid FlashcardSetRequest request
    ) {
        return ResponseBuilder.withData(HttpStatus.OK, "Cập nhật bộ thẻ thành công", service.update(id, request, thumbnail));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseBuilder.noData(HttpStatus.OK, "Xóa bộ thẻ thành công");
    }
}