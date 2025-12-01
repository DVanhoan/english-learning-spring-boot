package elearningspringboot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingRequest {

    @NotNull(message = "{validation.courseId.notNull}")
    private Long courseId;

    @NotNull(message = "{validation.rating.notNull}")
    @Min(value = 1, message = "{validation.rating.min}")
    @Max(value = 5, message = "{validation.rating.max}")
    private Integer rating;

    @Size(max = 1000, message = "{validation.message.size}")
    private String message;
}