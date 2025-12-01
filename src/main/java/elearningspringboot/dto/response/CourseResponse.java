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
public class CourseResponse extends BaseResponse {
    private String title;
    private String shortDescription;
    private String detailDescription;
    private String thumbnailUrl;
    private String learningOutcomes;
    private String requirements;
    private StatusCourse status;
    private Double price;
    private Double discountPrice;
    private Boolean isFree;
    private CategoryCourseResponse category;
    private UserSummaryResponse teacher;
    private Integer duration;
    private Integer numberOfLessons;
    private List<ChapterResponse> chaptersDetails;
    private Double averageRating;
    private Integer ratingCount;
    private Long firstLessonId;
}
