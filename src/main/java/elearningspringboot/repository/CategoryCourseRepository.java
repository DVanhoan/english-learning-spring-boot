package elearningspringboot.repository;

import elearningspringboot.entity.CategoryCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryCourseRepository extends JpaRepository<CategoryCourse, Long> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    @Query("select c from CategoryCourse c where lower(c.title) like %:keyword% OR lower(c.description) like %:keyword%")
    Page<CategoryCourse> searchCategories(@Param("keyword") String keyword, Pageable pageable);
}
