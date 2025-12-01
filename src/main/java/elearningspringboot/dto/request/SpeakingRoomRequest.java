package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SpeakingRoomRequest {
    @NotNull(message = "Topic ID không được để trống")
    private Long topicId;

    @NotBlank(message = "Tên phòng không được để trống")
    private String roomName;
}