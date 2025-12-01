package elearningspringboot.controller;

import elearningspringboot.dto.request.RatingRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.RatingResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.RatingCourseService;
import elearningspringboot.util.ResponseBuilder;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/ratings")
public class RatingCourseController {

    private final RatingCourseService ratingService;
    private final MessageSource messageSource;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ResponseData<RatingResponse>> createOrUpdateRating(
            @Valid @RequestBody RatingRequest request) {
        log.info("Request: Create or Update Rating for courseId: {}", request.getCourseId());
        RatingResponse response = ratingService.createOrUpdateRating(request);
        String message = messageSource.getMessage("rating.create.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResponseData<PageResponse<List<RatingResponse>>>> getRatingsByCourse(
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long courseId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "5") @Min(value = 1, message = "{validation.page.size.min}") int pageSize) {

        log.info("Request: Get ratings for courseId: {}, page: {}, size: {}", courseId, pageNumber, pageSize);
        PageResponse<List<RatingResponse>> response = ratingService.getRatingsByCourse(courseId, pageNumber, pageSize);
        String message = messageSource.getMessage("rating.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<ResponseData<Void>> deleteRating(
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long ratingId) {

        log.info("Request: Delete ratingId: {}", ratingId);
        ratingService.deleteRating(ratingId);
        String message = messageSource.getMessage("rating.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }
}