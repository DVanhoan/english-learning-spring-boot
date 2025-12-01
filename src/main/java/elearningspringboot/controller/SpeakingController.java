package elearningspringboot.controller;

import elearningspringboot.dto.request.SpeakingRoomRequest;
import elearningspringboot.dto.request.SpeakingTopicRequest;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.dto.response.SpeakingRoomResponse;
import elearningspringboot.dto.response.SpeakingTopicResponse;
import elearningspringboot.service.SpeakingService;
import elearningspringboot.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/speaking")
@RequiredArgsConstructor
public class SpeakingController {

    private final SpeakingService service;

    // --- PUBLIC API ---
    @GetMapping("/topics")
    public ResponseEntity<ResponseData<List<SpeakingTopicResponse>>> getAllTopics() {
        return ResponseBuilder.withData(HttpStatus.OK, "Success", service.getAllTopics());
    }

    @GetMapping("/rooms")
    public ResponseEntity<ResponseData<List<SpeakingRoomResponse>>> getActiveRooms() {
        return ResponseBuilder.withData(HttpStatus.OK, "Success", service.getActiveRooms());
    }

    @PostMapping("/rooms")
    public ResponseEntity<ResponseData<SpeakingRoomResponse>> createRoom(@RequestBody @Valid SpeakingRoomRequest request) {
        return ResponseBuilder.withData(HttpStatus.CREATED, "Room created", service.createRoom(request));
    }

    // --- ADMIN API ---

    @PostMapping(value = "/topics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SpeakingTopicResponse>> createTopic(
            @RequestPart("data") @Valid SpeakingTopicRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseBuilder.withData(HttpStatus.CREATED, "Topic created", service.createTopic(request, image));
    }

    @PutMapping(value = "/topics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SpeakingTopicResponse>> updateTopic(
            @PathVariable Long id,
            @RequestPart("data") @Valid SpeakingTopicRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseBuilder.withData(HttpStatus.OK, "Topic updated", service.updateTopic(id, request, image));
    }

    @DeleteMapping("/topics/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteTopic(@PathVariable Long id) {
        service.deleteTopic(id);
        return ResponseBuilder.noData(HttpStatus.OK, "Topic deleted");
    }
}