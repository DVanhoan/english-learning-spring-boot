package elearningspringboot.controller;

import elearningspringboot.dto.response.PayoutSummaryResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.PayoutService;
import elearningspringboot.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<PayoutSummaryResponse>>> getPayoutSummaries() {
        return ResponseBuilder.withData(HttpStatus.OK, "Success", payoutService.getPayoutSummaries());
    }

    @PostMapping("/{teacherId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> payoutToTeacher(@PathVariable Long teacherId) {
        payoutService.payoutToTeacher(teacherId);
        return ResponseBuilder.noData(HttpStatus.OK, "Thanh toán thành công");
    }
}