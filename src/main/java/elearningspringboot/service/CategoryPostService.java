package elearningspringboot.service;

import elearningspringboot.dto.request.CategoryPostRequest;
import elearningspringboot.dto.response.CategoryPostResponse;
import elearningspringboot.dto.response.PageResponse;

import java.util.List;

public interface CategoryPostService {
    CategoryPostResponse create(CategoryPostRequest request);

    CategoryPostResponse update(Long id, CategoryPostRequest request);

    void delete(Long id);

    CategoryPostResponse getById(Long id);

    PageResponse<List<CategoryPostResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            java.util.List<String> sorts, String keyword);
}
