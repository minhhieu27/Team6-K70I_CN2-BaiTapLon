package com.app.service.bid;

import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.user.UserNotFoundException;
import com.app.exception.wallet.InvalidBidException;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionNotifyService;
import com.app.service.auction.AuctionQuerryService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BidCoreService {

    private final AuctionQuerryService auctionQuerryService;

    private final BidRepository bidRepository;

    private final UserRepository userRepository;

    private final WalletService walletService;

    private final AuctionManagementService auctionManagementService;

    private final AuctionRepository auctionRepository;

    private final AuctionNotifyService auctionNotifyService;

    private final TransactionService transactionService;

    @Transactional
    public BidEntity excecuteBid(String auctionId, Money amount, String userId){
        AuctionEntity auction = auctionQuerryService.getEntityByAuctionId(auctionId);

        auctionManagementService.updateStatus(auction);
            
        if (auction.getStatus() != AuctionStatus.OPEN){
            throw new AuctionClosedException("Phiên đấu giá đã đóng");
        }

        if (!amount.isGreaterThan(auction.getCurrentPrice())){
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

        Money oldUserBidAmount = oldBid == null ? Money.zero() : oldBid.getAmount();

        Money extraToLock = amount.subtract(oldUserBidAmount);

        bidder.getWallet().lock(extraToLock);

        transactionService.createTransaction(bidder.getWallet(), extraToLock, TransactionType.BID_LOCK);

        // ====== TẠO BID ======
        BidEntity bid = new BidEntity(bidder, amount);

        auction.addBid(bid);

        auctionManagementService.extendTime(auction);

        // ====== HOÀN TIỀN LẠI KHI OUTBID ======
        if (oldHighestBidder != null && !oldHighestBidder.getUserId().equals(bidder.getUserId())){

            walletService.refundBid(oldHighestBidder, oldPrice);

            transactionService.createTransaction(oldHighestBidder.getWallet(), oldPrice, TransactionType.REFUND);
        }

        // ====== THÔNG BÁO ======
        auctionNotifyService.notifyNewBid(auction, bid);

        // ====== SAVE ======
        bidRepository.save(bid);

        auctionRepository.save(auction);

        userRepository.save(bidder);

        return bid;
    }
}
