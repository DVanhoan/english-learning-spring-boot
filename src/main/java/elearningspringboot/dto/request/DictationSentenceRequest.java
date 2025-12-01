package elearningspringboot.dto.request;

import lombok.Data;

@Data
public class DictationSentenceRequest {
    private String text;
    private Double startTime;
    private Double endTime;
    private Integer orderIndex;
}