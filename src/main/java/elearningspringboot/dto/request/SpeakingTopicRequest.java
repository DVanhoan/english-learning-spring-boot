package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpeakingTopicRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @NotBlank(message = "Cấp độ không được để trống")
    private String level; // Beginner, Intermediate, Advanced
}