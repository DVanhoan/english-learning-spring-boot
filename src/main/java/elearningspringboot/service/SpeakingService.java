package elearningspringboot.service;

import elearningspringboot.dto.request.SpeakingRoomRequest;
import elearningspringboot.dto.request.SpeakingTopicRequest;
import elearningspringboot.dto.response.SpeakingRoomResponse;
import elearningspringboot.dto.response.SpeakingTopicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpeakingService {
    // --- Topics ---
    List<SpeakingTopicResponse> getAllTopics();
    SpeakingTopicResponse getTopicById(Long id);
    SpeakingTopicResponse createTopic(SpeakingTopicRequest request, MultipartFile image);
    SpeakingTopicResponse updateTopic(Long id, SpeakingTopicRequest request, MultipartFile image);
    void deleteTopic(Long id);

    // --- Rooms ---
    List<SpeakingRoomResponse> getActiveRooms();
    SpeakingRoomResponse createRoom(SpeakingRoomRequest request);
    void closeRoom(Long roomId);
}