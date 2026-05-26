package com.app.common.strategy;

import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;

public interface BidStrategy {
    Money calculateMinBid(AuctionEntity auction);
    boolean isValidBid(AuctionEntity auction, BidEntity bid);
}
