package elearningspringboot.entity;

import elearningspringboot.enumeration.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 50)
    private String paymentGateway;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, unique = true)
    private String transactionCode; // Mã từ cổng thanh toán

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionDetail> details = new ArrayList<>();
}