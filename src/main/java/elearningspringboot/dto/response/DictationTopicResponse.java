package elearningspringboot.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictationTopicResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String levelRange;
    private String difficulty;
    private String thumbnailUrl;
    private Boolean hasVideo;
    private Integer lessonCount; // Số lượng bài học trong chủ đề
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}