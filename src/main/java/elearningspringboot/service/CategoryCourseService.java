package elearningspringboot.service;

import elearningspringboot.dto.request.CategoryCourseRequest;
import elearningspringboot.dto.response.CategoryCourseResponse;
import elearningspringboot.dto.response.PageResponse;

import java.util.List;

public interface CategoryCourseService {
    CategoryCourseResponse create(CategoryCourseRequest request);

    CategoryCourseResponse update(Long id, CategoryCourseRequest request);

    void delete(Long id);

    CategoryCourseResponse getById(Long id);

    List<CategoryCourseResponse> getAll();

    PageResponse<List<CategoryCourseResponse>> getCategoriesWithPaginationAndKeyword(int pageNumber, int pageSize,
            java.util.List<String> sorts, String keyword);
}
