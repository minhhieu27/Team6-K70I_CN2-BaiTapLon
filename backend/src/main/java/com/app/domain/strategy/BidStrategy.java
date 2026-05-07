package com.app.domain.strategy;

import com.app.domain.model.*;

public interface BidStrategy {
    Money calculateMinBid(Auction auction);
    boolean isValidBid(Auction auction, Bid bid);
}
