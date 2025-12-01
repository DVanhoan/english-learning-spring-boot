package elearningspringboot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentTransactionResponse {
    private String userFullName;
    private String userEmail;
    private String userAvatar;
    private Double amount;
    private LocalDateTime createdAt;
}