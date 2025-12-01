package elearningspringboot.mapper;

import elearningspringboot.dto.request.RatingRequest;
import elearningspringboot.dto.response.RatingResponse;
import elearningspringboot.entity.RatingCourse;
import elearningspringboot.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface RatingCourseMapper {

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "author", ignore = true)
    RatingCourse toEntity(RatingRequest request);

    @Mapping(source = "course.id", target = "courseId")
    RatingResponse toDTO(RatingCourse entity);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(RatingRequest request, @MappingTarget RatingCourse entity);

    default String map(Role role) {
        if (role == null) {
            return null;
        }

        return role.getRole().getName();
    }

}