package elearningspringboot.service;

import elearningspringboot.dto.response.PayoutSummaryResponse;
import java.util.List;

public interface PayoutService {
    List<PayoutSummaryResponse> getPayoutSummaries();
    void payoutToTeacher(Long teacherId);
}