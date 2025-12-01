package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaking_topics")
public class SpeakingTopic extends BaseEntity {

    @Column(nullable = false)
    private String title; // Ví dụ: "Hobbies", "Travel"

    @Column(columnDefinition = "TEXT")
    private String description; // Gợi ý câu hỏi thảo luận

    private String imageUrl;

    private String level; // Beginner, Intermediate...
}