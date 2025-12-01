package elearningspringboot.dto.response;

import lombok.Data;

@Data
public class FlashcardResponse {
    private Long id;
    private String term;
    private String definition;
}