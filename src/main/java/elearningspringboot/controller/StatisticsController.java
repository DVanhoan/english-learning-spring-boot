package elearningspringboot.controller;

import elearningspringboot.dto.response.DashboardStatisticsResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.StatisticsService;
import elearningspringboot.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<DashboardStatisticsResponse>> getDashboardStats() {
        DashboardStatisticsResponse stats = statisticsService.getDashboardStatistics();
        return ResponseBuilder.withData(HttpStatus.OK, "Lấy thống kê thành công", stats);
    }
}