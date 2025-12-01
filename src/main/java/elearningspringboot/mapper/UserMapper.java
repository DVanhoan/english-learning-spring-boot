package elearningspringboot.mapper;

import elearningspringboot.dto.request.AdminUserRequest;
import elearningspringboot.dto.request.UserRequest;
import elearningspringboot.dto.response.UserResponse;
import elearningspringboot.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gender", ignore = true)
    User fromAdminUserRequestToEntity(AdminUserRequest request);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gender", ignore = true)
    User fromUserRequestToEntity(UserRequest request);

    @Mapping(target = "role", ignore = true)
    UserResponse toDTO(User request);

    List<UserResponse> toListDTOs(List<User> request);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromAdminUserDTO(AdminUserRequest request, @MappingTarget User entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUserDTO(UserRequest request, @MappingTarget User entity);
}
