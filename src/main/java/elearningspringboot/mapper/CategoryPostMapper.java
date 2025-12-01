package elearningspringboot.mapper;

import elearningspringboot.dto.request.CategoryPostRequest;
import elearningspringboot.dto.response.CategoryPostResponse;
import elearningspringboot.entity.CategoryPost;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryPostMapper {
    @Mapping(target = "slug", ignore = true)
    CategoryPost toEntity(CategoryPostRequest request);

    CategoryPostResponse toDTO(CategoryPost entity);

    @Mapping(target = "slug", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CategoryPostRequest request, @MappingTarget CategoryPost entity);
}
