package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIChatResponse {
    private String userText;      // Những gì bạn nói (đã chuyển thành chữ)
    private String aiText;        // Câu trả lời của AI
    private String aiAudioUrl;    // Link file âm thanh giọng nói của AI
}