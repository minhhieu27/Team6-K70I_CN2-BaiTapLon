package auction.strategy;

import auction.model.*;

public interface BidStrategy {
    Money calculateMinBid(Auction auction);
    boolean isValidBid(Auction auction, Bid bid);
}
