package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaking_rooms")
public class SpeakingRoom extends BaseEntity {

    private String roomName;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private SpeakingTopic topic;

    private int currentParticipants = 0;
}