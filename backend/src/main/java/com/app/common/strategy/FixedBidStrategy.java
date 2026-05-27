package com.app.common.strategy;

import org.springframework.stereotype.Component;

import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;

@Component
public class FixedBidStrategy implements BidStrategy {
    
    @Override
    public Money calculateMinBid(AuctionEntity auction){

        Money startPrice = auction.getItem().getStartPrice();
        Money step;

        if (startPrice.isGreaterThan(new Money(5000000000L)) || startPrice.isEqual(new Money(5000000000L))){
            step = new Money(100000000);
    
        }else if (startPrice.isGreaterThan(new Money(500000000)) || startPrice.isEqual(new Money(500000000))){
            step = new Money(50000000);
        
        }else if (startPrice.isGreaterThan(new Money(50000000)) || startPrice.isEqual(new Money(50000000))){
            step = new Money(3000000);

        } else {
            step = new Money(200000);
        }

        return auction.getCurrentPrice().add(step);
    }

    @Override
    public boolean isValidBid(AuctionEntity auction, BidEntity bid){
        Money minBid = calculateMinBid(auction);

        return bid.getAmount().isGreaterThan(minBid) || bid.getAmount().isEqual(minBid);
    }
}
