package com.app.service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.dto.response.BidResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.wallet.InvalidBidException;
import com.app.mapper.BidMapper;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;

@Service
public class BidService {
    
    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidMapper bidMapper;

    @Autowired 
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    // ====== PLACE BID ======
    public BidResponse placeBid(String auctionId, Money amount, String userId) {

        AuctionEntity auction = auctionService.getEntityByAuctionId(auctionId);

        auctionService.updateStatus(auction);
       
        if (auction.getStatus() != AuctionStatus.OPEN){
            throw new AuctionClosedException("Auction đã đóng");
        }

        if (auction.getCurrentPrice().isGreaterThan(amount)){
            throw new InvalidBidException("Giá đấu giá phải lớn hơn giá hiện tại");
        }

        UserEntity bidder = userService.getEntityByUserId(userId);

        BidEntity oldBid = bidRepository.findTopByBidderAndAuctionOrderByAmountDesc(bidder, auction).orElse(null);

        Money oldAmount = oldBid == null ? Money.isZero() : oldBid.getAmount();

        Money extreToLock = amount.subtract(oldAmount);

        bidder.getWallet().lock(extreToLock);

        // ====== CREATE BID ======
        BidEntity bid = new BidEntity(bidder, amount);

        bid.setAuction(auction);

        auction.addBid(bid);

        auctionService.extendTime(auction);

        bidRepository.save(bid);

        auctionService.save(auction);

        userRepository.save(bidder);

        return bidMapper.toResponse(bid);
    }
}
