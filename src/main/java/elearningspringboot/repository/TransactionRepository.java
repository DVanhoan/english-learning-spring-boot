package elearningspringboot.repository;

import elearningspringboot.entity.Transaction;
import elearningspringboot.enumeration.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Tìm giao dịch PENDING bằng mã
    Optional<Transaction> findByTransactionCodeAndStatus(String transactionCode, TransactionStatus status);

    // Kiểm tra xem mã giao dịch (thành công) đã được xử lý chưa
    boolean existsByTransactionCodeAndStatus(String transactionCode, TransactionStatus status);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'SUCCESS'")
    Double sumTotalRevenue();

    // Lấy 5 giao dịch thành công gần nhất
    List<Transaction> findTop5ByStatusOrderByCreatedAtDesc(TransactionStatus status);
}