package elearningspringboot.controller;

import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.dto.response.UserSummaryResponse;
import elearningspringboot.service.EnrollmentService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.ResponseBuilder;
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
@RequestMapping("/api/v1")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final MessageSource messageSource;

    @PostMapping("/courses/{courseId}/enroll/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseResponse>> adminAddEnrollment(
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long courseId,
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long studentId) {
        log.info("Request: Admin add enroll studentId={} to courseId={}", studentId, courseId);
        CourseResponse res = enrollmentService.enroll(courseId, studentId);
        String message = messageSource.getMessage("enrollment.success.enrolled", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, res);
    }

    @DeleteMapping("/courses/{courseId}/enroll/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> unEnrollCourse(
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long courseId,
            @PathVariable @Min(value = 1, message = "{validation.id.min}") Long studentId) {
        log.info("Request: Un enroll studentId={} from courseId={}", studentId, courseId);
        enrollmentService.unEnrollCourse(courseId, studentId);
        String message = messageSource.getMessage("enrollment.success.unenrolled", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/enrollments/me")
    public ResponseEntity<ResponseData<PageResponse<List<CourseResponse>>>> getMyEnrollments(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {
        Long studentId = AppUtils.getUserIdFromSecurityContext();
        log.info(
                "Request: Get enrollments for studentId={}, pageNumber={}, pageSize={}, sorts={}, keyword={}, categoryId={}",
                studentId, pageNumber, pageSize, sorts, keyword, categoryId);
        PageResponse<List<CourseResponse>> page = enrollmentService.getEnrollmentsForStudent(studentId, pageNumber,
                pageSize, sorts, keyword, categoryId);
        String message = messageSource.getMessage("enrollment.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, page);
    }

    @GetMapping("/courses/{courseId}/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ResponseData<PageResponse<List<UserSummaryResponse>>>> getStudentsByCourse(
            @PathVariable("courseId") @Min(value = 1, message = "{validation.id.min}") Long courseId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword) {
        log.info("Request: Get students for courseId={}, pageNumber={}, pageSize={}, sorts={}, keyword={}",
                courseId, pageNumber, pageSize, sorts, keyword);
        PageResponse<List<UserSummaryResponse>> page = enrollmentService.getStudentsByCourse(courseId, pageNumber,
                pageSize, sorts, keyword);
        String message = messageSource.getMessage("enrollment.students.get.list.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, page);
    }
}
