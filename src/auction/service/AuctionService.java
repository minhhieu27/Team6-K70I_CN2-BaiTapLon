package auction.service;

import auction.exception.AuctionClosedException;
import auction.exception.InvalidBidException;
import auction.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class AuctionService {
    private static final double BID_INCREMENT = 1.1;
    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;

    public void placeBid(Auction auction, Bid bid) throws InvalidBidException, AuctionClosedException{
       
        if (!isOpen(auction)){
            throw new AuctionClosedException("Auction is closed");
        }

        if (bid.getAmount() <= auction.getCurrentPrice() * BID_INCREMENT){
            throw new InvalidBidException("Bid too low");
        }

        auction.addBid(bid);
        extendTime(auction);
    }

    public boolean isOpen(Auction auction){
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(auction.getStartTime()) && now.isBefore(auction.getEndTime());
    }

    private void extendTime(Auction auction){
        long secondLeft = Duration.between(LocalDateTime.now(), auction.getEndTime()).getSeconds();

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));
        }
    }
}
