package elearningspringboot.mapper;

import elearningspringboot.dto.request.CategoryCourseRequest;
import elearningspringboot.dto.response.CategoryCourseResponse;
import elearningspringboot.entity.CategoryCourse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryCourseMapper {
    @Mapping(target = "slug", ignore = true)
    CategoryCourse toEntity(CategoryCourseRequest request);

    @Mapping(target = "courseCount", expression = "java(entity.getCourses() == null ? 0 : entity.getCourses().size())")
    CategoryCourseResponse toDTO(CategoryCourse entity);


    @Mapping(target = "slug", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CategoryCourseRequest request, @MappingTarget CategoryCourse entity);

    List<CategoryCourseResponse> toListDTOs(List<CategoryCourse> entities);
}
