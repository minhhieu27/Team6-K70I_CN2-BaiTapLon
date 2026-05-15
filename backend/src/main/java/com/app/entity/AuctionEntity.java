package com.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.strategy.BidStrategy;
import com.app.common.strategy.FixedBidStrategy;
import com.app.common.tool.IDGenerator;
import com.app.exception.wallet.InvalidBidException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auctions")
public class AuctionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (name = "auction_id",nullable = false, unique = true)
    private String auctionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String itemName;

    @Column(length = 1000)
    private String description;

    @Transient
    private BidStrategy bidStrategy = new FixedBidStrategy();

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "start_price", nullable = false))
    private Money startPrice;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "current_price", nullable = false))
    private Money currentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserEntity seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status = AuctionStatus.SCHEDULED;

    @Column(name = "start_time")
    private LocalDateTime startTime;


    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BidEntity> bidHistory = new ArrayList<>();

    public AuctionEntity(String title, String itemName, String description, Money startPrice, UserEntity seller, LocalDateTime startTime){
        this.auctionId = IDGenerator.generateAuctionId();

        this.title = title;
        this.itemName = itemName;
        this.description = description;

        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        
        this.seller = seller;

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(30);
    }

    public void addBid(BidEntity bid){
        
        if (bid == null){
            throw new InvalidBidException("Bid không được trống");
        }

        if (!bidStrategy.isValidBid(this, bid)){
            throw new InvalidBidException("Đấu giá không hợp lệ");
        }

        bid.setAuction(this);

        this.currentPrice = bid.getAmount();

        bidHistory.add(bid);
    }

    public void setStatus(AuctionStatus status){
        this.status = status;
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setSeller(UserEntity seller){
        this.seller = seller;
    }

    public void setStartPrice(Money startPrice){
        this.startPrice = startPrice;
    }

    public void setBidStrategy(BidStrategy bidStrategy){
        this.bidStrategy = bidStrategy;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
