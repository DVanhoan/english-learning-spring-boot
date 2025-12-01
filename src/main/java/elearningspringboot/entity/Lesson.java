package elearningspringboot.entity;

import elearningspringboot.enumeration.StatusCourse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer duration;
    private String content;
    private String videoUrl;
    private String attachmentUrl;
    private Boolean isPreview;
    @Enumerated(EnumType.STRING)
    private StatusCourse status;
    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
}
