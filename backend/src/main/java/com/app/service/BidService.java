package com.app.service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.dto.response.BidResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.user.UserNotFoundException;
import com.app.exception.wallet.InvalidBidException;
import com.app.mapper.BidMapper;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;

@Service
public class BidService {
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BidMapper bidMapper;

    @Autowired 
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    public List<BidResponse> getBidHistory(String auctionId) {
        AuctionEntity auction = auctionService.getEntityByAuctionId(auctionId);

        return bidRepository.findByAuctionOrderByCreateBidAtDesc(auction)
                .stream()
                .map(bidMapper::toResponse)
                .toList();
    }

    // ====== ĐẤU GIÁ ======
    public BidResponse placeBid(String auctionId, Money amount, String userId) {

        AuctionEntity auction = auctionService.getEntityByAuctionId(auctionId);

        auctionService.updateStatus(auction);
       
        if (auction.getStatus() != AuctionStatus.OPEN){
            throw new AuctionClosedException("Auction đã đóng");
        }

        if (auction.getCurrentPrice().isGreaterThan(amount)){
            throw new InvalidBidException("Giá đấu giá phải lớn hơn giá hiện tại");
        }

        UserEntity bidder = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        // ====== TÌM GIÁ CAO NHẤT HIỆN TẠI  ======
        BidEntity highestBid = bidRepository.findTopByAuctionOrderByAmount_ValueDesc(auction).orElse(null);

        // ====== TÌM GIÁ CŨ ======
        BidEntity oldBid = bidRepository.findTopByUserAndAuctionOrderByAmount_ValueDesc(bidder, auction).orElse(null);

        // ====== TÍNH TIỀN KHÓA THÊM ======

        Money oldAmount = oldBid == null ? Money.isZero() : oldBid.getAmount();

        Money extraToLock = amount.subtract(oldAmount);

        bidder.getWallet().lock(extraToLock);

        // ====== TẠO BID ======
        BidEntity bid = new BidEntity(bidder, amount);

        bid.setAuction(auction);

        auction.addBid(bid);

        auctionService.extendTime(auction);

        // ====== HOÀN TIỀN LẠI KHI OUTBID ======
        if (highestBid != null && !highestBid.getUser().equals(bidder)){

            walletService.refundBid(highestBid.getUser(), highestBid.getAmount());
        }

        // ====== THÔNG BÁO ======
        for (UserEntity follower : auction.getFollowers()){

            notificationService.notifyUser(follower, bidder.getUserId() + " đã đấu giá " + amount.getValue() + " cho auction " + auction.getTitle());
        }

        // ====== SAVE ======
        bidRepository.save(bid);

        auctionService.save(auction);

        userRepository.save(bidder);

        return bidMapper.toResponse(bid);
    }
}
