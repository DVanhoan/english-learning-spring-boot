package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CreatePaymentRequest {
    @NotEmpty(message = "Danh sách khóa học không được để trống")
    private List<Long> courseIds;
}