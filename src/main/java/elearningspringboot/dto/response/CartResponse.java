package elearningspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private List<CourseResponse> items;
    private int totalItems;
    private double subtotal; // Tổng tiền (sau giảm giá)
    private double totalOriginalPrice; // Tổng tiền (gốc)
}