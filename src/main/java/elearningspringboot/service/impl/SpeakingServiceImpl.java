package elearningspringboot.service.impl;

import elearningspringboot.dto.request.SpeakingRoomRequest;
import elearningspringboot.dto.request.SpeakingTopicRequest;
import elearningspringboot.dto.response.SpeakingRoomResponse;
import elearningspringboot.dto.response.SpeakingTopicResponse;
import elearningspringboot.entity.SpeakingRoom;
import elearningspringboot.entity.SpeakingTopic;
import elearningspringboot.entity.User;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.SpeakingMapper;
import elearningspringboot.repository.SpeakingRoomRepository;
import elearningspringboot.repository.SpeakingTopicRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.SpeakingService;
import elearningspringboot.service.StorageService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeakingServiceImpl implements SpeakingService {

    private final SpeakingTopicRepository topicRepository;
    private final SpeakingRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final SpeakingMapper mapper;

    // ============================================================
    // TOPIC MANAGEMENT
    // ============================================================

    @Override
    public List<SpeakingTopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(mapper::toTopicDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SpeakingTopicResponse getTopicById(Long id) {
        SpeakingTopic entity = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speaking Topic not found"));
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public SpeakingTopicResponse createTopic(SpeakingTopicRequest request, MultipartFile image) {
        SpeakingTopic entity = mapper.toTopicEntity(request);

        if (image != null && !image.isEmpty()) {
            String url = storageService.uploadFile(image);
            entity.setImageUrl(url);
        }
        topicRepository.save(entity);
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public SpeakingTopicResponse updateTopic(Long id, SpeakingTopicRequest request, MultipartFile image) {
        SpeakingTopic entity = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speaking Topic not found"));

        mapper.updateTopicFromRequest(request, entity);

        if (image != null && !image.isEmpty()) {
            String url = storageService.uploadFile(image);
            entity.setImageUrl(url);
        }

        topicRepository.save(entity);
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Topic not found");
        }
        topicRepository.deleteById(id);
    }

    // ============================================================
    // ROOM MANAGEMENT
    // ============================================================

    @Override
    public List<SpeakingRoomResponse> getActiveRooms() {
        return roomRepository.findByIsActiveTrue().stream()
                .map(mapper::toRoomDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SpeakingRoomResponse createRoom(SpeakingRoomRequest request) {
        Long userId = AppUtils.getUserIdFromSecurityContext();
        User host = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SpeakingTopic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        SpeakingRoom room = new SpeakingRoom();
        room.setRoomName(request.getRoomName());
        room.setTopic(topic);
        room.setHost(host);
        room.setActive(true);
        room.setCurrentParticipants(1);

        roomRepository.save(room);
        return mapper.toRoomDTO(room);
    }

    @Override
    @Transactional
    public void closeRoom(Long roomId) {
        SpeakingRoom room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            room.setActive(false);
            roomRepository.save(room);
        }
    }
}