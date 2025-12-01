package elearningspringboot.repository;

import elearningspringboot.entity.Course;
import elearningspringboot.entity.Enrollment;
import elearningspringboot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    @Query("select e.course from Enrollment e " +
            "where e.student.id = :studentId " +
            "and (:categoryId is null or e.course.category.id = :categoryId) " +
            "and (:keyword = '' or lower(e.course.title) like concat('%', :keyword, '%') " +
            "or lower(e.course.shortDescription) like concat('%', :keyword, '%') " +
            "or lower(e.course.detailDescription) like concat('%', :keyword, '%'))")
    Page<Course> findCoursesByStudentIdAndKeyword(@Param("studentId") Long studentId,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("select e.student from Enrollment e " +
            "where e.course.id = :courseId " +
            "and (:keyword = '' or lower(e.student.fullName) like concat('%', :keyword, '%') " +
            "or lower(e.student.email) like concat('%', :keyword, '%'))")
    Page<User> findStudentsByCourseIdAndKeyword(@Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            Pageable pageable);

}

