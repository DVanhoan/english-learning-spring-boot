package elearningspringboot.entity;

import elearningspringboot.enumeration.StatusPost;
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
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_title", columnList = "title"),
        @Index(name = "idx_posts_tags", columnList = "tags"),
})
public class Post extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    @Enumerated(EnumType.STRING)
    private StatusPost status;

    private String thumbnailUrl;

    @Column(length = 255)
    private String tags;

    private String slug;

    private Integer readingTimeMinutes;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryPost category;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}