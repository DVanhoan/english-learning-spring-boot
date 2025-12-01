package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardStatisticsResponse {
    // 1. Stats Cards
    private Double totalRevenue;
    private Long totalUsers;
    private Long totalEnrollments;
    private Long newUsersToday; // Thay cho Active Now

    // 2. Recent Sales
    private List<RecentTransactionResponse> recentTransactions;

    // 3. Course Stats
    private Long totalCourses;
    private Long publishedCourses;
    private Long draftCourses; // Nếu có status DRAFT

    // 4. Content Stats
    private Long totalPosts;
    private Long totalFlashcardSets;
    private Long totalDictationLessons;
}