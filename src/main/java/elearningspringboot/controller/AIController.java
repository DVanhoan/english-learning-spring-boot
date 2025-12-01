package elearningspringboot.controller;

import elearningspringboot.dto.response.AIChatResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.impl.GeminiService;
import elearningspringboot.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {

    private final GeminiService geminiService;

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<AIChatResponse>> chatWithAI(
            @RequestPart("audio") MultipartFile audio) {

        AIChatResponse response = geminiService.processVoiceChat(audio);
        return ResponseBuilder.withData(HttpStatus.OK, "Success", response);
    }
}