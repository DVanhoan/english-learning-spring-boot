package elearningspringboot.repository;

import elearningspringboot.entity.RatingCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingCourseRepository extends JpaRepository<RatingCourse, Long> {
    Optional<RatingCourse> findByCourseIdAndAuthorId(Long courseId, Long authorId);
    Page<RatingCourse> findByCourseId(Long courseId, Pageable pageable);
}
