package elearningspringboot.mapper;

import elearningspringboot.dto.request.CourseRequest;
import elearningspringboot.dto.response.CourseResponse;
import elearningspringboot.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CategoryCourseMapper.class })
public interface CourseMapper {
    @Mapping(target = "status", ignore = true)
    Course toEntity(CourseRequest request);

    @Mapping(target = "teacher", ignore = true)
    CourseResponse toDTO(Course entity);

    @Mapping(target = "status", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CourseRequest request, @MappingTarget Course entity);

}
