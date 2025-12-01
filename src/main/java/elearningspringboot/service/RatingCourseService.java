package elearningspringboot.service;

import elearningspringboot.dto.request.RatingRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.RatingResponse;

import java.util.List;

public interface RatingCourseService {
    RatingResponse createOrUpdateRating(RatingRequest request);
    PageResponse<List<RatingResponse>> getRatingsByCourse(Long courseId, int pageNumber, int pageSize);
    void deleteRating(Long ratingId);
}