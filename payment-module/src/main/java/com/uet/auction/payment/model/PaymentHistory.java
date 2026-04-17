package com.uet.auction.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Double amount;
    private String status; // VD: "SUCCESS", "FAILED"
    private String description; // VD: "Thanh toán đấu giá món đồ A"

    @CreationTimestamp
    private LocalDateTime createdAt; // Tự động lưu thời gian giao dịch
}