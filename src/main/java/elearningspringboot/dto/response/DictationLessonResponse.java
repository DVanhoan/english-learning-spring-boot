package elearningspringboot.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictationLessonResponse {
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String vocabLevel; // A1, B1...
    private String mediaUrl;   // URL video/audio
    private String duration;
    private Long topicId;      // ID của chủ đề cha
    private List<DictationSentenceResponse> sentences; // Danh sách câu
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}