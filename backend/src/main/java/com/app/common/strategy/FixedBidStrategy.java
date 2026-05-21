package com.app.common.strategy;

import org.springframework.stereotype.Component;

import com.app.common.money.Money;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;

@Component
public class FixedBidStrategy implements BidStrategy {
    
    @Override
    public Money calculateMinBid(AuctionEntity auction){

        Money startPrice = auction.getStartPrice();
        Money step;

        if (startPrice.isGreaterThan(new Money(500000)) || startPrice.isEqual(new Money(500000))){
            step = new Money(1000);
        }
        else if (startPrice.isGreaterThan(new Money(10000)) || startPrice.isEqual(new Money(10000))){
            step = new Money(400);
        } else {
            step = new Money(20);
        }

        return auction.getCurrentPrice().add(step);
    }

    @Override
    public boolean isValidBid(AuctionEntity auction, BidEntity bid){
        Money minBid = calculateMinBid(auction);
        return bid.getAmount().isGreaterThan(minBid) || bid.getAmount().isEqual(minBid);
    }
}
