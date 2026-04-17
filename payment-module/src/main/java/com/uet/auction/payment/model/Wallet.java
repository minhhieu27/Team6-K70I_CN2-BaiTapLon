package com.uet.auction.payment.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wallets")
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