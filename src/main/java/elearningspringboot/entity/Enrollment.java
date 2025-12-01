package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments")
@Builder
public class Enrollment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
