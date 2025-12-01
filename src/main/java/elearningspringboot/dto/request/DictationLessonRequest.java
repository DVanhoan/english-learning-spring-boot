package elearningspringboot.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class DictationLessonRequest {
    private String title;
    private String subtitle;
    private String description;
    private String vocabLevel; // A1, B1...
    private Long topicId;
    private String duration;

    // Admin gửi danh sách câu kèm timestamp
    private List<DictationSentenceRequest> sentences;
}