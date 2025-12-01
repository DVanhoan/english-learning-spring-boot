package elearningspringboot.service;

import elearningspringboot.dto.response.DashboardStatisticsResponse;

public interface StatisticsService {
    DashboardStatisticsResponse getDashboardStatistics();
}