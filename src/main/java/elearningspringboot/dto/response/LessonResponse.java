package elearningspringboot.dto.response;

import elearningspringboot.enumeration.StatusCourse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LessonResponse extends BaseResponse {
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer duration;
    private String content;
    private String videoUrl;
    private String attachmentUrl;
    private Boolean isPreview;
    private StatusCourse status;
    private Long chapterId;
}
