package elearningspringboot.entity;

import elearningspringboot.enumeration.StatusCourse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chapters")
public class Chapter extends BaseEntity {
    private String title;
    private String description;
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    private StatusCourse status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE)
    private List<Lesson> lessons;
}
