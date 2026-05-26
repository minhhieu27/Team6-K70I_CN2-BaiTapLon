package com.app.entity.auction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.strategy.BidStrategy;
import com.app.common.strategy.FixedBidStrategy;
import com.app.entity.user.UserEntity;
import com.app.exception.wallet.InvalidBidException;
import com.app.entity.bid.BidEntity;
import com.app.entity.item.ItemEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "auctions")
public class AuctionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "auction_id",nullable = false, unique = true)
    private String auctionId;

    @Column(nullable = false)
    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Transient
    private BidStrategy bidStrategy = new FixedBidStrategy();

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "current_price", nullable = false))
    private Money currentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, referencedColumnName = "user_id")
    private UserEntity seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status = AuctionStatus.SCHEDULED;

    @ManyToMany
    @JoinTable(name = "auction_followers", joinColumns = @JoinColumn(name = "auction_id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private List<UserEntity> followers = new ArrayList<>();

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BidEntity> bidHistory = new ArrayList<>();

    @Column (name = "paid_status", nullable = false )
    private boolean paid = false;

    @Column (name = "notified_ending", nullable = true)
    private LocalDateTime lastEndingNotificationAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id")
    private UserEntity highestBidder;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImageEntity> images = new ArrayList<>();

    @Version
    private Long version;

    public AuctionEntity(String title, ItemEntity item, UserEntity seller, LocalDateTime startTime){

        this.title = title;
        this.item = item;

        this.currentPrice = item.getStartPrice();
        
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

        this.highestBidder = bid.getUser();

        bidHistory.add(bid);
    }

    public void addFollower(UserEntity user){
        followers.add(user);
    }

    public void removeFollower(UserEntity user){
        followers.remove(user);
    }

    public void addImage(AuctionImageEntity image){

        image.setAuction(this);

        images.add(image);
    }
}