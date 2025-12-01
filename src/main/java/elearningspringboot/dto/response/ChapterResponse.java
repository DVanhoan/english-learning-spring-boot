package elearningspringboot.dto.response;

import elearningspringboot.enumeration.StatusCourse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChapterResponse extends BaseResponse {
    private String title;
    private String description;
    private Integer orderIndex;
    private StatusCourse status;
    private Integer numberOfLessons;
    private Integer duration;
    private List<LessonResponse> lessonsDetails;
}
