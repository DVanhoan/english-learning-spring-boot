package elearningspringboot.service;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.ChapterRequest;
import elearningspringboot.dto.response.ChapterResponse;
import elearningspringboot.dto.response.PageResponse;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface ChapterService {
    ChapterResponse create(ChapterRequest request);

    ChapterResponse update(Long id, ChapterRequest request);

    void delete(Long id);

    ChapterResponse getById(Long id);

    List<ChapterResponse> getChaptersByCourseId(Long courseId);

    void updateOrder(Long id, List<ChangeOrderRequest> request);
}
