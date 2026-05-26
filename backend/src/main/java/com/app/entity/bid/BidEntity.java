package com.app.entity.bid;

import java.time.LocalDateTime;

import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bids")
public class BidEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private UserEntity user;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount", nullable = false))
    private Money amount;

    private LocalDateTime createBidAt = LocalDateTime.now();

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private AuctionEntity auction;

    public BidEntity(UserEntity user, Money amount){
        this.user = user;
        this.amount = amount;
    }

    public void setAuction(AuctionEntity auction){
        this.auction = auction;
    }
}
