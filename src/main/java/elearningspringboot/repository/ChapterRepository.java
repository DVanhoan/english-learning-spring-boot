package elearningspringboot.repository;

import elearningspringboot.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    @Query("select c from Chapter c left join fetch c.lessons where :courseId is null or c.course.id = :courseId order by c.orderIndex asc")
    List<Chapter> searchByCourseId(@Param("courseId") Long courseId);

    @Query("select coalesce(max(c.orderIndex), 0) from Chapter c where c.course.id = :courseId")
    Integer getOrderIndexMax(Long courseId);
}
