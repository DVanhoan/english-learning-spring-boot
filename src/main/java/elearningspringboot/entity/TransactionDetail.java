package elearningspringboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction_details")
public class TransactionDetail extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Double price; // Giá tại thời điểm mua

    @Column(nullable = false)
    private Double commissionRate; // Chiết khấu tại thời điểm mua

    @OneToOne(mappedBy = "transactionDetail", cascade = CascadeType.ALL)
    private TeacherPayout teacherPayout;
}