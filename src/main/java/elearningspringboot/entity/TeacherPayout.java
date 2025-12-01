package elearningspringboot.entity;

import elearningspringboot.enumeration.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher_payouts")
public class TeacherPayout extends BaseEntity {

    @Column(nullable = false)
    private Double amountEarned; // Tiền Teacher nhận

    @Column(nullable = false)
    private Double platformFee; // Tiền bạn nhận (phí nền tảng)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PayoutStatus status = PayoutStatus.UNPAID;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToOne
    @JoinColumn(name = "transaction_detail_id", unique = true)
    private TransactionDetail transactionDetail;
}