package auction.strategy;

import auction.model.Auction;
import auction.model.Bid;

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
        return bid.getAmount().isGreaterThan(minBid);
    }
}
