package elearningspringboot.mapper;

import elearningspringboot.dto.request.PostRequest;
import elearningspringboot.dto.response.PostResponse;
import elearningspringboot.entity.CategoryPost;
import elearningspringboot.entity.Post;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CategoryPostMapper.class })
public interface PostMapper {
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "readingTimeMinutes", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "author", ignore = true)
    Post toEntity(PostRequest request);

    @Mapping(target = "author", ignore = true)
    PostResponse toDTO(Post entity);

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "readingTimeMinutes", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "author", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PostRequest request, @MappingTarget Post entity);
}