package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

import java.util.List;

@Service
public class BidService {
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    @Lazy
    private AuctionService auctionService;

    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    @Lazy
    private AutoBidService autoBidService;

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
            throw new AuctionClosedException("Phiên đấu giá đã đóng");
        }

        if (auction.getCurrentPrice().isGreaterThan(amount)){
            throw new InvalidBidException("Giá đấu giá phải lớn hơn giá hiện tại");
        }

        if (auction.getSeller().getUserId().equals(userId)){
            throw new InvalidBidException("Người bán không thể tham gia đấu giá");
        }

        UserEntity bidder = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));
        bidder.validateActive();

        // ====== LƯU GIÁ CAO NHẤT HIỆN TẠI TRƯỚC KHI CÓ BID MỚI ======
        Money oldPrice = auction.getCurrentPrice();

        UserEntity oldHighestBidder = auction.getHighestBidder();

        // ====== TÌM GIÁ CŨ CAO CỦA NGƯỜI ĐÃ ĐẤU GIÁ ======
        BidEntity oldBid = bidRepository.findTopByUserAndAuctionOrderByAmount_ValueDesc(bidder, auction).orElse(null);

        // ====== TÍNH TIỀN KHÓA THÊM ======

        Money oldUserBidAmount = oldBid == null ? Money.isZero() : oldBid.getAmount();

        Money extraToLock = amount.subtract(oldUserBidAmount);

        bidder.getWallet().lock(extraToLock);

        // ====== TẠO BID ======
        BidEntity bid = new BidEntity(bidder, amount);

        auction.addBid(bid);

        auctionService.extendTime(auction);

        // ====== HOÀN TIỀN LẠI KHI OUTBID ======
        if (oldHighestBidder != null && !oldHighestBidder.getUserId().equals(bidder.getUserId())){

            walletService.refundBid(oldHighestBidder, oldPrice);
        }

        // ====== THÔNG BÁO ======
        for (UserEntity follower : auction.getFollowers()){

            notificationService.notifyUser(follower, bidder.getUserId() + " đã đấu giá " + amount.getValue() + " cho auction " + auction.getTitle());
        }

        // ====== SAVE ======
        bidRepository.save(bid);

        autoBidService.processAutoBid(auction);

        auctionService.save(auction);

        userRepository.save(bidder);

        return bidMapper.toResponse(bid);
    }
}
