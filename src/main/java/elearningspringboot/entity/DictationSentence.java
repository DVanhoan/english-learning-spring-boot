package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dictation_sentences")
public class DictationSentence extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; // Nội dung câu đúng

    private Double startTime; // Thời gian bắt đầu (giây), ví dụ: 0.0
    private Double endTime;   // Thời gian kết thúc (giây), ví dụ: 5.5

    private Integer orderIndex; // Thứ tự câu

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private DictationLesson lesson;
}