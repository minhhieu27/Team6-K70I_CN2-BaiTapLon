package com.uet.auction.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
// Lưu lịch sử giao dịch của người dùng
@Entity// tạo bảng trong database
@Data
public class PaymentHistory {
    // Mỗi lần người dùng thanh toán sẽ tạo một bản ghi mới trong bảng này
    @Id
    // ID tự tăng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Double amount;
    private String status; // VD: "SUCCESS", "FAILED"
    private String description; // VD: "Thanh toán đấu giá món đồ A"
    // Thời gian giao dịch, tự động lưu khi tạo bản ghi mới
    @CreationTimestamp
    private LocalDateTime createdAt; // Tự động lưu thời gian giao dịch
}