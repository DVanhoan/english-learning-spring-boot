package elearningspringboot.service;

import elearningspringboot.dto.request.FlashcardSetRequest;
import elearningspringboot.dto.response.FlashcardSetResponse;
import elearningspringboot.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FlashcardService {

    FlashcardSetResponse create(FlashcardSetRequest request, MultipartFile thumbnail);

    FlashcardSetResponse update(Long id, FlashcardSetRequest request, MultipartFile thumbnail);

    void delete(Long id);

    FlashcardSetResponse getById(Long id);
    PageResponse<List<FlashcardSetResponse>> getAll(int page, int size, String keyword, String category, String sort);

    PageResponse<List<FlashcardSetResponse>> getAllForManagement(int page, int size, String keyword, String category, String sort, Long authorId);
}