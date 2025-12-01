package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SpeakingRoomResponse {
    private Long id;
    private String roomName;
    private boolean isActive;
    private int currentParticipants;
    private UserSummaryResponse host;
    private SpeakingTopicResponse topic;
    private LocalDateTime createdAt;
}