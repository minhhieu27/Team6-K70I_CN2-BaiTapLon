package com.app.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bids")
public class Bid {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
