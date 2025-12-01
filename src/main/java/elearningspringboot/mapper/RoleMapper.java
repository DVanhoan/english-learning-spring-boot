package elearningspringboot.mapper;

import elearningspringboot.dto.request.RoleRequest;
import elearningspringboot.dto.response.RoleResponse;
import elearningspringboot.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "role", ignore = true)
    Role toEntity(RoleRequest request);

    RoleResponse toDTO(Role entity);

    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(RoleRequest request, @MappingTarget Role role);
}
