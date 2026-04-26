package auction.strategy;

import auction.model.Auction;
import auction.model.Bid;
import auction.model.Money;

public class FixedIncrementStrategy implements BidStrategy {
    
    private final Money increment;

    public FixedIncrementStrategy(Money increment){
        this.increment = increment;
    }

    @Override
    public Money calculateMinBid(Auction auction){
        return auction.getCurrentPrice().add(increment);
    }

    @Override
    public boolean isValidBid(Auction auction, Bid bid){
        return bid.getAmount().isGreaterThan(calculateMinBid(auction));
    }
}
