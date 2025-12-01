package elearningspringboot.dto.request;

import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import elearningspringboot.validation.ValueOfEnum;
import elearningspringboot.enumeration.StatusCourse;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterRequest {
    @NotBlank(message = "{validation.chapter.title.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.chapter.title.size}", groups = { OnCreate.class, OnUpdate.class })
    private String title;

    @Size(max = 1000, message = "{validation.chapter.description.size}", groups = { OnCreate.class, OnUpdate.class })
    private String description;

    @ValueOfEnum(enumClass = StatusCourse.class, message = "{validation.chapter.status.invalid}", groups = {
            OnCreate.class, OnUpdate.class })
    private String status;

    @NotNull(message = "{validation.chapter.course.required}", groups = { OnCreate.class })
    private Long courseId;
}
