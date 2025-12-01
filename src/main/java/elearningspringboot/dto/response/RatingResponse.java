package elearningspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Long id;
    private Integer rating;
    private String message;
    private Long courseId;
    private UserSummaryResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}