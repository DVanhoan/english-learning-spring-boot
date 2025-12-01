package elearningspringboot.entity;

import elearningspringboot.enumeration.StatusCourse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {
    private String title;
    private String shortDescription;
    private String detailDescription;
    private String thumbnailUrl;
    private String learningOutcomes;
    private String requirements;

    @Enumerated(EnumType.STRING)
    private StatusCourse status;

    private Double price;
    private Double discountPrice;
    private Boolean isFree;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryCourse category;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Chapter> chapters;

    @Column(nullable = false, columnDefinition = "double default 0.3")
    private Double commissionRate;

    @Formula("(SELECT AVG(r.rating) FROM ratings r WHERE r.course_id = id)")
    private Double averageRating;

    @Formula("(SELECT COUNT(r.id) FROM ratings r WHERE r.course_id = id)")
    private Integer ratingCount;
}
