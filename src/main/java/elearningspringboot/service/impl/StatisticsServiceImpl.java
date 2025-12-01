package elearningspringboot.service.impl;

import elearningspringboot.dto.response.DashboardStatisticsResponse;
import elearningspringboot.dto.response.RecentTransactionResponse;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.StatusCourse;
import elearningspringboot.enumeration.TransactionStatus;
import elearningspringboot.repository.*;
import elearningspringboot.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final PostRepository postRepository;
    private final FlashcardSetRepository flashcardSetRepository;
    private final DictationLessonRepository dictationLessonRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics() {
        // 1. Revenue & Sales
        Double revenue = transactionRepository.sumTotalRevenue();
        if (revenue == null) revenue = 0.0;

        Long totalEnrollments = enrollmentRepository.count();

        // 2. Users
        Long totalUsers = userRepository.count(); // Hoặc countByStatus(Status.ACTIVE)
        Long newUsersToday = userRepository.countNewUsersToday();

        // 3. Recent Transactions
        List<RecentTransactionResponse> recentTransactions = transactionRepository
                .findTop5ByStatusOrderByCreatedAtDesc(TransactionStatus.SUCCESS)
                .stream()
                .map(t -> RecentTransactionResponse.builder()
                        .userFullName(t.getStudent().getFullName())
                        .userEmail(t.getStudent().getEmail())
                        .userAvatar(t.getStudent().getAvatarUrl())
                        .amount(t.getAmount())
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // 4. Courses
        Long totalCourses = courseRepository.count();
        Long publishedCourses = courseRepository.countByStatus(StatusCourse.PUBLIC);
        Long draftCourses = totalCourses - publishedCourses; // Giả sử còn lại là Draft/Hidden

        // 5. Content
        Long totalPosts = postRepository.count();
        Long totalFlashcards = flashcardSetRepository.count();
        Long totalDictation = dictationLessonRepository.count();

        return DashboardStatisticsResponse.builder()
                .totalRevenue(revenue)
                .totalUsers(totalUsers)
                .totalEnrollments(totalEnrollments)
                .newUsersToday(newUsersToday)
                .recentTransactions(recentTransactions)
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .draftCourses(draftCourses)
                .totalPosts(totalPosts)
                .totalFlashcardSets(totalFlashcards)
                .totalDictationLessons(totalDictation)
                .build();
    }
}