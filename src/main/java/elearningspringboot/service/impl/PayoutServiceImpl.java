package elearningspringboot.service.impl;

import elearningspringboot.dto.response.PayoutSummaryResponse;
import elearningspringboot.entity.TeacherPayout;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.PayoutStatus;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.repository.TeacherPayoutRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private final TeacherPayoutRepository payoutRepository;
    private final UserRepository userRepository;

    @Override
    public List<PayoutSummaryResponse> getPayoutSummaries() {
        List<TeacherPayout> allPayouts = payoutRepository.findAll();

        // Nhóm theo Teacher
        Map<User, List<TeacherPayout>> groupedByTeacher = allPayouts.stream()
                .collect(Collectors.groupingBy(TeacherPayout::getTeacher));

        List<PayoutSummaryResponse> responses = new ArrayList<>();

        for (Map.Entry<User, List<TeacherPayout>> entry : groupedByTeacher.entrySet()) {
            User teacher = entry.getKey();
            List<TeacherPayout> payouts = entry.getValue();

            double unpaid = payouts.stream()
                    .filter(p -> p.getStatus() == PayoutStatus.UNPAID)
                    .mapToDouble(TeacherPayout::getAmountEarned)
                    .sum();

            double paid = payouts.stream()
                    .filter(p -> p.getStatus() == PayoutStatus.PAID)
                    .mapToDouble(TeacherPayout::getAmountEarned)
                    .sum();

            responses.add(PayoutSummaryResponse.builder()
                    .teacherId(teacher.getId())
                    .teacherName(teacher.getFullName())
                    .teacherEmail(teacher.getEmail())
                    .totalUnpaid(unpaid)
                    .totalPaid(paid)
                    .build());
        }
        return responses;
    }

    @Override
    @Transactional
    public void payoutToTeacher(Long teacherId) {
        List<TeacherPayout> unpaidPayouts = payoutRepository.findByTeacherIdAndStatus(teacherId, PayoutStatus.UNPAID);

        if (unpaidPayouts.isEmpty()) {
            throw new ResourceNotFoundException("Không có khoản thanh toán nào cần xử lý cho giảng viên này.");
        }

        // Đánh dấu là PAID
        for (TeacherPayout payout : unpaidPayouts) {
            payout.setStatus(PayoutStatus.PAID);
        }
        payoutRepository.saveAll(unpaidPayouts);
    }
}