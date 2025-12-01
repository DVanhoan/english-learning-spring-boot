package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flashcard_sets")
public class FlashcardSet extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // Ví dụ: "TOEIC", "IELTS"

    private String thumbnailUrl;

    private Boolean isPublic = true; // Mặc định công khai

    // Số lượt học và đánh giá (có thể update sau)
    private Integer studyCount = 0;
    private Double rating = 0.0;
    private Integer ratingCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    // Quan hệ 1-N với Flashcard
    // orphanRemoval = true: Nếu xóa thẻ khỏi list này, nó sẽ bị xóa khỏi DB
    @OneToMany(mappedBy = "flashcardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flashcard> flashcards = new ArrayList<>();
}