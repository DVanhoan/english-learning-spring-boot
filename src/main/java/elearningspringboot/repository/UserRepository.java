package elearningspringboot.repository;

import elearningspringboot.entity.User;
import elearningspringboot.enumeration.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByEmail(String email);

        @Query("select u from User u where u.role.role = elearningspringboot.enumeration.UserRole.TEACHER and u.id = :id")
        Optional<User> findTeacherById(@Param("id") Long id);

        @Query("select u from User u where u.role.role = elearningspringboot.enumeration.UserRole.TEACHER")
        List<User> findAllTeachers();

        @Query("select distinct u from User u join fetch u.role r where u.email = :email")
        Optional<User> findByEmailWithRole(@Param("email") String email);

        boolean existsByEmail(String email);

        @Query("select u from User u where lower(u.fullName) like %:keyword% OR lower(u.email) like %:keyword%")
        Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

        @Transactional
        @Modifying
        @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :expiredAt")
        void deleteExpiredPendingUsers(@Param("status") Status status,
                        @Param("expiredAt") LocalDateTime expiredAt);

        @Query("select u.noPassword from User u where u.email = :email")
        Boolean getStatusPassword(@Param("email") String email);

        long countByStatus(Status status);

        @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= CURRENT_DATE")
        long countNewUsersToday();
}
