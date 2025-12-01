package elearningspringboot.mapper;

import elearningspringboot.dto.request.SpeakingTopicRequest;
import elearningspringboot.dto.response.SpeakingRoomResponse;
import elearningspringboot.dto.response.SpeakingTopicResponse;
import elearningspringboot.entity.Role;
import elearningspringboot.entity.SpeakingRoom;
import elearningspringboot.entity.SpeakingTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface SpeakingMapper {

    // --- Topic ---
    SpeakingTopicResponse toTopicDTO(SpeakingTopic entity);

    SpeakingTopic toTopicEntity(SpeakingTopicRequest request);

    void updateTopicFromRequest(SpeakingTopicRequest request, @MappingTarget SpeakingTopic entity);

    // --- Room ---
    @Mapping(target = "host", source = "host") // UserMapper sẽ tự map User sang UserSummaryResponse
    @Mapping(target = "topic", source = "topic") // MapStruct tự map Topic sang TopicResponse
    SpeakingRoomResponse toRoomDTO(SpeakingRoom entity);

    default String map(Role role) {
        if (role == null || role.getRole() == null) {
            return null;
        }
        return role.getRole().name();
    }
}