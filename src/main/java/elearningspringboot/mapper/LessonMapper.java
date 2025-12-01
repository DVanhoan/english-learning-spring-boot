package elearningspringboot.mapper;

import elearningspringboot.dto.request.LessonRequest;
import elearningspringboot.dto.response.LessonResponse;
import elearningspringboot.entity.Lesson;
import elearningspringboot.enumeration.StatusCourse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = {StatusCourse.class}, uses = {ChapterMapper.class})
public interface LessonMapper {

    @Mapping(target="content", ignore = true)
    @Mapping(target="description", ignore = true)
    @Mapping(target = "status",expression = "java(StatusCourse.fromValue(request.getStatus()))")
    Lesson toEntity(LessonRequest request);

    LessonResponse toDTO(Lesson entity);

    List<LessonResponse> toDTOs(List<Lesson> entities);

    @Mapping(target = "status",expression = "java(StatusCourse.fromValue(request.getStatus()))")
    @Mapping(target="content", ignore = true)
    @Mapping(target="description", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(LessonRequest request, @MappingTarget Lesson entity);
}
