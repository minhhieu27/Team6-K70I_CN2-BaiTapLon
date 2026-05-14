package com.app.service.strategy;

import com.app.common.money.Money;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;

public interface BidStrategy {
    Money calculateMinBid(AuctionEntity auction);
    boolean isValidBid(AuctionEntity auction, BidEntity bid);
}
