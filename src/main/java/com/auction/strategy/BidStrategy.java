package auction.strategy;

public interface BidStrategy {
    Money calculateMinBid(Auction auction);
    boolean isValidBid(Auction auction, Bid bid);
}
