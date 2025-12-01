package elearningspringboot.service;

import elearningspringboot.dto.request.ChangeOrderRequest;
import elearningspringboot.dto.request.LessonRequest;
import elearningspringboot.dto.response.LessonResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonService {
    LessonResponse update(Long id, LessonRequest request, MultipartFile attachment, MultipartFile video);

    void delete(Long id);

    LessonResponse getByIdForAdminAndTeacher(Long id);

    List<LessonResponse> getLessonsByChapterId(Long chapterId);

    LessonResponse create(LessonRequest request, MultipartFile attachment, MultipartFile video);

    void updateOrder( Long chapterId, List<ChangeOrderRequest> request);
}
