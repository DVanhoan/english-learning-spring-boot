package elearningspringboot.repository;

import elearningspringboot.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Lấy tất cả item trong giỏ của một user
    List<CartItem> findByStudentId(Long studentId);

    // Kiểm tra xem item đã có trong giỏ chưa
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // Tìm item để xóa
    Optional<CartItem> findByStudentIdAndCourseId(Long studentId, Long courseId);
}