package elearningspringboot.service;

import elearningspringboot.dto.request.CourseRequest;
import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.enumeration.StatusCourse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    CourseResponse create(CourseRequest request, MultipartFile thumbnail);

    CourseResponse update(Long id, CourseRequest request, MultipartFile thumbnail);

    void delete(Long id);

    CourseResponse getById(Long id);

    CourseResponse getDetailsCourseById(Long id);

    PageResponse<List<CourseResponse>> getAllWithPagination(int pageNumber, int pageSize, List<String> sorts,
            String keyword, Long categoryId, Long teacherId, StatusCourse status);

    // New: return newest public courses limited by n
    List<CourseResponse> getNewest(int n);
    List<CourseResponse> getCoursesByIds(List<Long> ids);
}
