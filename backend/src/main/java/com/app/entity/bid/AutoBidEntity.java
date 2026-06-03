package com.app.entity.bid;

import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.AutoBidClosedException;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "auto_bid", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "auction_id"})})
@Getter
@Setter
@NoArgsConstructor
public class AutoBidEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String autoBid;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private AuctionEntity auction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "max_amount", nullable = false))
    private Money maxAmount;

    private boolean active = true;

    public AutoBidEntity(UserEntity user, AuctionEntity auction, Money maxAmount){
        this.user = user;
        this.auction = auction;
        this.maxAmount = maxAmount;
    }

    public void deactivate(){
        this.active = false;
    }

    public void activate(){
        this.active = true;
    }

    public void validateActive(){
        if (!active){
            throw new AutoBidClosedException("Auto bid đã tắt");
        }
    }
}