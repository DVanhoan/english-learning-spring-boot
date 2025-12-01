package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SpeakingTopicResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}