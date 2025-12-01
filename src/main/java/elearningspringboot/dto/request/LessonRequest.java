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
public class LessonRequest {
    @NotBlank(message = "{validation.lesson.title.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.lesson.title.size}", groups = { OnCreate.class, OnUpdate.class })
    private String title;

    @Size(max = 1000, message = "{validation.lesson.description.size}", groups = { OnCreate.class, OnUpdate.class })
    private String description;

    @NotBlank(message = "{validation.lesson.content.not.blank}", groups = { OnCreate.class, OnUpdate.class })
    private String content;

    @NotNull(message = "{validation.lesson.isPreview.not.null}", groups = { OnCreate.class, OnUpdate.class })
    private Boolean isPreview;

    private Integer duration;

    @ValueOfEnum(enumClass = StatusCourse.class, message = "{validation.lesson.status.invalid}", groups = {
            OnCreate.class, OnUpdate.class })
    private String status;

    @NotNull(message = "{validation.lesson.chapter.required}", groups = { OnCreate.class })
    private Long chapterId;
}
