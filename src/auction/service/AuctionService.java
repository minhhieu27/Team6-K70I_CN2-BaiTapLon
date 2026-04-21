package auction.service;

import auction.exception.AuctionClosedException;
import auction.exception.InvalidBidException;
import auction.model.*;
import auction.strategy.BidStrategy;
import auction.util.Validator;

import java.time.Duration;
import java.time.LocalDateTime;

public class AuctionService {
    private final BidStrategy strategy;
    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;

    public AuctionService(BidStrategy strategy){
        this.strategy = strategy;
    }

    public void placeBid(Auction auction, Bid bid) throws AppException {
        Validator.validateBid(bid.getAmount());

        updateStatus(auction);
       
        if (auction.geStatus() != AuctionStatus.OPEN){
            throw new AuctionClosedException("Auction is closed");
        }

        if (!auction.getStrategy().isValidBid(auction, bid)){
            throw new InvalidBidException("Invalid bid");
        }

        auction.addBid(bid);
        extendTime(auction);
    }

   public void updateStatus(Auction auction) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())){
            auction.setStatus(AuctionStatus.SCHEDULED);

        } else if (now.isAfter(auction.getEndTime())){
            auction.setStatus(AuctionStatus.FINISHED);

        } else {
            auction.setStatus(AuctionStatus.OPEN);
        }
   }

    private void extendTime(Auction auction){
        long secondLeft = Duration.between(LocalDateTime.now(), auction.getEndTime()).getSeconds();

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));
        }
    }
}
