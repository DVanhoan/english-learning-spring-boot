package elearningspringboot.dto.request;

import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCourseRequest {

    @NotBlank(message = "{validation.categoryCourse.title.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.categoryCourse.title.size}", groups = { OnCreate.class, OnUpdate.class })
    private String title;

    @Size(max = 1000, message = "{validation.categoryCourse.description.size}", groups = { OnCreate.class,
            OnUpdate.class })
    private String description;
}
