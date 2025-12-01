package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class FlashcardSetRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @NotBlank(message = "Danh mục không được để trống")
    private String category;

    private Boolean isPublic = true;

    @NotEmpty(message = "Phải có ít nhất một thẻ")
    private List<FlashcardRequest> cards;
}