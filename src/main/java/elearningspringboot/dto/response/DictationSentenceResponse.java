package elearningspringboot.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictationSentenceResponse {
    private Long id;
    private String text;
    private Double startTime;
    private Double endTime;
    private Integer orderIndex;
}