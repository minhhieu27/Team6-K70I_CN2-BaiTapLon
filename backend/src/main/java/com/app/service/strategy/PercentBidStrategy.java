package com.app.service.strategy;

import com.app.common.money.Money;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;

public class PercentBidStrategy implements BidStrategy {
    
    private final double percentage;

    public PercentBidStrategy(double percentage){
        this.percentage = percentage;
    }

    @Override
    public Money calculateMinBid(AuctionEntity auction){
        return auction.getCurrentPrice().multiply(percentage);
    }

    @Override
    public boolean isValidBid(AuctionEntity auction, BidEntity bid){
        Money minBid = calculateMinBid(auction);
        return bid.getAmount().isGreaterThan(minBid) || bid.getAmount().isEqual(minBid);
    }
}
