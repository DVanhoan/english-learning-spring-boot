package elearningspringboot.dto.request;

import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import elearningspringboot.validation.ValueOfEnum;
import elearningspringboot.validation.OnUserCreate;
import elearningspringboot.enumeration.StatusPost;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "{validation.post.title.notBlank}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    @Size(max = 255, message = "{validation.post.title.size}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    private String title;

    @NotBlank(message = "{validation.post.excerpt.notBlank}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    @Size(max = 1000, message = "{validation.post.excerpt.size}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    private String excerpt;

    @NotBlank(message = "{validation.post.content.notBlank}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    private String content;

    @NotNull(message = "{validation.post.category.required}", groups = { OnCreate.class, OnUpdate.class,
            OnUserCreate.class })
    private Long categoryId;

    @NotNull(message = "{validation.post.author.required}", groups = { OnCreate.class, OnUpdate.class })
    private Long authorId;

    @ValueOfEnum(enumClass = StatusPost.class, message = "{validation.post.status.invalid}", groups = {
            OnCreate.class, OnUpdate.class })
    private String status;
}