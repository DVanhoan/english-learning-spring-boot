package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dictation_topics")
public class DictationTopic extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // Ví dụ: "TOEIC", "IELTS"
    private String levelRange; // Ví dụ: "A1-B1"
    private String difficulty; // "Easy", "Medium", "Hard"
    private String thumbnailUrl;

    // Có video hay không (để hiển thị icon play/video)
    private Boolean hasVideo = false;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<DictationLesson> lessons;

    @ManyToOne
    @JoinColumn(name = "author_id") // Thêm trường này
    private User author;
}