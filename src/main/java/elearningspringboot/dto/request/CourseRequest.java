package elearningspringboot.dto.request;

import elearningspringboot.validation.ValueOfEnum;
import elearningspringboot.enumeration.StatusCourse;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    @NotBlank(message = "{validation.course.title.notBlank}")
    @Size(max = 255, message = "{validation.course.title.size}")
    private String title;

    @NotBlank(message = "{validation.course.shortDescription.notBlank}")
    private String shortDescription;

    @NotBlank(message = "{validation.course.detailDescription.notBlank}")
    private String detailDescription;

    @NotBlank(message = "{validation.course.learningOutcomes.notBlank}")
    private String learningOutcomes;

    @NotBlank(message = "{validation.course.requirements.notBlank}")
    private String requirements;

    @ValueOfEnum(enumClass = StatusCourse.class, message = "{validation.course.status.invalid}")
    private String status;

    @NotNull(message = "{validation.course.price.not.null}")
    @PositiveOrZero(message = "{validation.course.price.invalid}")
    private Double price;

    @NotNull(message = "{validation.course.discount.price.not.null}")
    @PositiveOrZero(message = "{validation.course.discountPrice.invalid}")
    private Double discountPrice;

    @NotNull(message = "{validation.course.category.required}")
    private Long categoryId;

    @NotNull(message = "{validation.course.teacher.required}")
    private Long teacherId;

    @NotNull(message = "{validation.course.isFree.required}")
    private Boolean isFree;
}
