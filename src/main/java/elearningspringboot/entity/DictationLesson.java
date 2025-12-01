package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dictation_lessons")
public class DictationLesson extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String subtitle; // Mô tả ngắn hoặc chủ đề phụ

    @Column(columnDefinition = "TEXT")
    private String description;

    private String vocabLevel; // A1, B1, ...

    // URL file âm thanh/video trên Cloudinary
    private String mediaUrl;

    // Thời lượng (ví dụ: "05:30")
    private String duration;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private DictationTopic topic;

    // Danh sách các câu (để chấm điểm và sync audio)
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<DictationSentence> sentences;
}