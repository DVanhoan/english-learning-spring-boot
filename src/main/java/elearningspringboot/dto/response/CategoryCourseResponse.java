package elearningspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CategoryCourseResponse extends BaseResponse {
    private String title;
    private String description;
    private String slug;
    private int courseCount;
}
