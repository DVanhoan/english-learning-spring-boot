package elearningspringboot.repository;

import elearningspringboot.entity.FlashcardSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, Long> {

    @Query("SELECT f FROM FlashcardSet f " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "lower(f.title) LIKE lower(concat('%', :keyword, '%')) OR " +
            "lower(f.description) LIKE lower(concat('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR :category = 'Tất cả' OR f.category = :category) " +
            "AND (f.isPublic = true OR f.author.id = :userId)")
    Page<FlashcardSet> searchFlashcards(@Param("keyword") String keyword,
                                        @Param("category") String category,
                                        @Param("userId") Long userId,
                                        Pageable pageable);

    // Đếm số lượng bài của user
    long countByAuthorId(Long authorId);

    @Query("SELECT f FROM FlashcardSet f " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "lower(f.title) LIKE lower(concat('%', :keyword, '%')) OR " +
            "lower(f.description) LIKE lower(concat('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR :category = 'Tất cả' OR f.category = :category) " +
            "AND (:authorId IS NULL OR f.author.id = :authorId)")
    Page<FlashcardSet> findAllForManagement(@Param("keyword") String keyword,
                                            @Param("category") String category,
                                            @Param("authorId") Long authorId,
                                            Pageable pageable);
}