package elearningspringboot.service;

import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.UserSummaryResponse;

import java.util.List;

public interface EnrollmentService {
    CourseResponse enroll(Long courseId, Long studentId);

    void unEnrollCourse(Long courseId, Long studentId);

    List<CourseResponse> getEnrollmentsForStudent(Long studentId);

    PageResponse<List<CourseResponse>> getEnrollmentsForStudent(Long studentId, int pageNumber, int pageSize,
            List<String> sorts, String keyword, Long categoryId);

    PageResponse<List<UserSummaryResponse>> getStudentsByCourse(Long courseId, int pageNumber, int pageSize,
            List<String> sorts, String keyword);
}
