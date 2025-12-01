package elearningspringboot.entity;

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
@Table(name = "categories_course")
public class CategoryCourse extends BaseEntity {
    private String title;

    private String description;

    @Column(unique = true)
    private String slug;

    @OneToMany(mappedBy = "category")
    private List<Course> courses;
}
