package elearningspringboot.service;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DictationService {
    // Topic
    List<DictationTopicResponse> getAllTopics();
    List<DictationTopicResponse> getTopicsByAuthor(Long authorId);
    DictationTopicResponse getTopicById(Long id);
    DictationTopicResponse createTopic(DictationTopicRequest request, MultipartFile thumbnail);
    DictationTopicResponse updateTopic(Long id, DictationTopicRequest request, MultipartFile thumbnail);
    void deleteTopic(Long id);

    // Lesson
    List<DictationLessonResponse> getLessonsByTopicId(Long topicId);
    DictationLessonResponse getLessonById(Long id);
    DictationLessonResponse createLesson(DictationLessonRequest request, MultipartFile audioFile);
    DictationLessonResponse updateLesson(Long id, DictationLessonRequest request, MultipartFile audioFile);
    void deleteLesson(Long id);
}