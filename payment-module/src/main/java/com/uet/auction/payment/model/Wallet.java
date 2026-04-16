package com.uet.auction.payment.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wallets")
@Data // Nếu bị gạch đỏ thì nhấn Ctrl+Shift+O để import
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private Double balance;
}