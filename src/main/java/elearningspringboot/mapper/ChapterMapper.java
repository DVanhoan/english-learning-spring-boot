package elearningspringboot.mapper;

import elearningspringboot.dto.request.ChapterRequest;
import elearningspringboot.dto.response.ChapterResponse;
import elearningspringboot.entity.Chapter;
import elearningspringboot.enumeration.StatusCourse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = {StatusCourse.class})
public interface ChapterMapper {
    @Mapping(target = "status", expression = "java(StatusCourse.fromValue(request.getStatus()))")
    Chapter toEntity(ChapterRequest request);

    ChapterResponse toDTO(Chapter entity);

    List<ChapterResponse> toDTOs(List<Chapter> entities);

    @Mapping(target = "status", expression = "java(StatusCourse.fromValue(request.getStatus()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ChapterRequest request, @MappingTarget Chapter entity);
}
