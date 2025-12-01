package elearningspringboot.dto.request;

import lombok.Data;

@Data
public class DictationTopicRequest {
    private String title;
    private String description;
    private String category;
    private String levelRange;
    private String difficulty;
    private Boolean hasVideo;
}