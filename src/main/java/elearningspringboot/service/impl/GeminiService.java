package elearningspringboot.service.impl;

import elearningspringboot.dto.request.GeminiRequest;
import elearningspringboot.dto.response.AIChatResponse;
import elearningspringboot.dto.response.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AIChatResponse processVoiceChat(MultipartFile audioFile) {
        try {
            String base64Audio = Base64.getEncoder().encodeToString(audioFile.getBytes());

            String systemPrompt = "You are a friendly English tutor. " +
                    "Listen to the user's audio and reply naturally to keep the conversation going. " +
                    "Do NOT repeat or transcribe what the user said. Just give the answer directly. " +
                    "Keep your response short and simple (A2-B1 level).";

            GeminiRequest.Part textPart = GeminiRequest.Part.builder()
                    .text(systemPrompt)
                    .build();

            GeminiRequest.Part audioPart = GeminiRequest.Part.builder()
                    .inlineData(GeminiRequest.InlineData.builder()
                            .mimeType(audioFile.getContentType())
                            .data(base64Audio)
                            .build())
                    .build();

            GeminiRequest requestBody = GeminiRequest.builder()
                    .contents(Collections.singletonList(
                            GeminiRequest.Content.builder()
                                    .parts(List.of(textPart, audioPart))
                                    .build()
                    ))
                    .build();

            String url = apiUrl + "?key=" + apiKey;
            GeminiResponse response = restTemplate.postForObject(url, requestBody, GeminiResponse.class);

            String fullResponse = "Sorry, I couldn't hear that.";
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                fullResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }

            return AIChatResponse.builder()
                    .userText("(Audio Message)")
                    .aiText(fullResponse)
                    .aiAudioUrl(null)
                    .build();

        } catch (HttpClientErrorException e) {
            log.error("Gemini API Error: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 429) {
                throw new RuntimeException("Hệ thống AI đang quá tải, vui lòng thử lại sau 1 phút.");
            }
            throw new RuntimeException("Lỗi kết nối AI: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Internal Error: ", e);
            throw new RuntimeException("Lỗi hệ thống: " + e.getMessage());
        }
    }
}