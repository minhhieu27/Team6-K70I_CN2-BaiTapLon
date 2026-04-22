package com.uet.auction.payment.model;

import jakarta.persistence.*;
import lombok.Data;
// Ví điện tử của người dùng, lưu số dư hiện tại
@Entity
// Tạo bảng "wallets" trong database để lưu thông tin ví của người dùng
@Table(name = "wallets")
// @Data tự động tạo getter, setter, toString, equals, hashCode
@Data
public class Wallet {
    @Id
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Double balance;

    @Version // QUAN TRỌNG: JPA sẽ dùng cột này để chống tranh chấp
    private Integer version;
}