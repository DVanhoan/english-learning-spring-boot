package elearningspringboot.repository;

import elearningspringboot.entity.TeacherPayout;
import elearningspringboot.enumeration.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherPayoutRepository extends JpaRepository<TeacherPayout, Long> {

    @Query("SELECT SUM(tp.amountEarned) FROM TeacherPayout tp WHERE tp.teacher.id = :teacherId AND tp.status = 'UNPAID'")
    Double getUnpaidEarningsByTeacherId(@Param("teacherId") Long teacherId);

    @Modifying
    @Query("UPDATE TeacherPayout tp SET tp.status = 'PAID' WHERE tp.teacher.id = :teacherId AND tp.status = 'UNPAID'")
    void markAllAsPaidByTeacherId(@Param("teacherId") Long teacherId);

    List<TeacherPayout> findAll();
    List<TeacherPayout> findByTeacherIdAndStatus(Long teacherId, PayoutStatus status);
}