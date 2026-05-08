package com.app.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.Auction;
import com.app.entity.Bid;
import com.app.repository.BidRepository;

@Service
public class BidService {
    
    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BidRepository bidRepository;

    public String placeBid(String auctionId, BigDecimal amount, String userId){
        Auction auction = auctionService.getByAuctionId(auctionId);

        auction.setCurrentPrice(amount);
        auctionService.save(auction);

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setAmount(amount);
        bid.setUserId(userId);

        bidRepository.save(bid);

        return "Bid thành công";
    }
}
