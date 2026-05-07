package com.app.domain.strategy;

import com.app.domain.model.*;

public class PercentBidStrategy implements BidStrategy {
    
    private final double percentage;

    public PercentBidStrategy(double percentage){
        this.percentage = percentage;
    }

    @Override
    public Money calculateMinBid(Auction auction){
        return auction.getCurrentPrice().multiply(percentage);
    }

    @Override
    public boolean isValidBid(Auction auction, Bid bid){
        Money minBid = calculateMinBid(auction);
        return bid.getAmount().isGreaterThanOrEqual(minBid);
    }
}
