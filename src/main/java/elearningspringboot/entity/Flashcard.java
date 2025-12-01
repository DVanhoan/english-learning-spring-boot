package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flashcards")
public class Flashcard extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String term; // Mặt trước (Thuật ngữ/Câu hỏi)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String definition; // Mặt sau (Định nghĩa/Câu trả lời)

    @ManyToOne
    @JoinColumn(name = "flashcard_set_id")
    private FlashcardSet flashcardSet;
}