package com.app.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "auctions")
public class Auction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (unique = true)
    private String auctionId;

    private String title;

    @Column(precision = 19, scale = 2)
    private BigDecimal startPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal currentPrice;

    private String userId; // userid

    private String status;
}
