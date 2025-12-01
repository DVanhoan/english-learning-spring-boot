package elearningspringboot.repository;

import elearningspringboot.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("select l from Lesson l where l.chapter.id = :chapterId order by l.orderIndex asc")
    List<Lesson> findByChapterId(Long chapterId);

    @Query("select coalesce(max(l.orderIndex), 0) from Lesson l where l.chapter.id = :chapterId")
    Integer getOrderIndexMax(Long chapterId);
}
