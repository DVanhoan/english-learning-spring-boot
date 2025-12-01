package elearningspringboot.dto.request;

import lombok.Data;

@Data
public class FlashcardRequest {
    private String term;
    private String definition;
}