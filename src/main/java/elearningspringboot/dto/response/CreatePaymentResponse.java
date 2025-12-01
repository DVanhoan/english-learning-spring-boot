package elearningspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePaymentResponse {
    private String paymentUrl;
}