package com.app.domain.service;

import java.time.LocalDateTime;

import com.app.domain.enums.AuctionStatus;
import com.app.domain.exception.BusinessError.*;
import com.app.domain.exception.base.AppException;
import com.app.domain.model.*;
import com.app.domain.strategy.BidStrategy;
import com.app.domain.tool.DateTimeUtil;
import com.app.domain.tool.Validator;

public class AuctionService {
    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;
    
    public AuctionService(BidStrategy strategy){
    }

    public void placeBid(Auction auction, Bid bid) throws AppException {
        Validator.validateBid(bid.getAmount());

        updateStatus(auction);
       
        if (auction.getStatus() != AuctionStatus.OPEN){
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
        long secondLeft = DateTimeUtil.secondLeft(auction.getEndTime());

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));
        }
    }
}
