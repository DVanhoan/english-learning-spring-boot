package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayoutSummaryResponse {
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private String bankInfo; // Nếu User có trường này, hiện tại tạm để trống
    private Double totalUnpaid; // Tiền chờ trả
    private Double totalPaid;   // Tiền đã trả
}