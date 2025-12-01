package elearningspringboot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SpeakingSignalController {

    // 1. Khi Client muốn tham gia phòng: /app/join/{roomId}
    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/room/{roomId}") // Gửi thông báo cho tất cả người trong phòng
    public Map<String, Object> joinRoom(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        log.info("User {} joined room {}", payload.get("userId"), roomId);
        // payload chứa: userId, userName, avatar...
        payload.put("type", "JOIN");
        return payload;
    }

    // 2. Trao đổi tín hiệu WebRTC (Offer): /app/offer/{roomId}
    @MessageMapping("/offer/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> sendOffer(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        // payload chứa: type="OFFER", sdp, senderId, receiverId
        return payload;
    }

    // 3. Trao đổi tín hiệu WebRTC (Answer): /app/answer/{roomId}
    @MessageMapping("/answer/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> sendAnswer(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        // payload chứa: type="ANSWER", sdp, senderId, receiverId
        return payload;
    }

    // 4. Trao đổi ICE Candidate: /app/candidate/{roomId}
    @MessageMapping("/candidate/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> sendCandidate(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        // payload chứa: type="CANDIDATE", candidate, senderId, receiverId
        return payload;
    }

    // 5. Rời phòng
    @MessageMapping("/leave/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> leaveRoom(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        payload.put("type", "LEAVE");
        return payload;
    }
}