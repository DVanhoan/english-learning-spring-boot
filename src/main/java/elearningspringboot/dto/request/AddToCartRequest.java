package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull(message = "Course ID không được để trống")
    private Long courseId;
}