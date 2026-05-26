package com.app.service.auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.NoUserJoinAuctionException;
import com.app.repository.AuctionRepository;
import com.app.service.bid.AutoBidService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;


@Service
public class AuctionPaymentService {
    
    @Autowired
    private WalletService walletService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    @Lazy
    private AutoBidService autoBidService;

    @Autowired
    private AuctionNotifyService auctionNotifyService;

    @Autowired
    private TransactionService transactionService;

    // ====== SETTLE ======
    public void settleAuction(AuctionEntity auction){

        if (auction.isPaid()){
            return;
        }

        // ====== WINNER ======
        UserEntity winner = auction.getHighestBidder();

        if (winner == null){
            throw new NoUserJoinAuctionException("Không có người tham gia đấu giá");
        }

        auctionNotifyService.notifyWinner(auction);

        UserEntity seller = auction.getSeller();

        Money originalAmount = auction.getCurrentPrice();

        Money finalAmount = calculateDiscountPrice(winner, originalAmount);

        Money refund = originalAmount.subtract(finalAmount);

        if (!refund.isZero()){

            walletService.refundBid(winner, refund);
        }

        // ====== PAYMENT ======
        
        walletService.paySeller(winner, seller, finalAmount);

        winner.getWallet().addSpent(finalAmount);

        transactionService.createTransaction(winner.getWallet(), finalAmount, TransactionType.PAYMENT);

        transactionService.createTransaction(seller.getWallet(), finalAmount, TransactionType.RECEIVE);

        transactionService.createTransaction(winner.getWallet(), refund, TransactionType.REFUND);

        // ====== NOTIFICATION ======
        auctionNotifyService.notifyAuctionFinished(auction);
        
        auctionNotifyService.notifyWinner(auction);

        auctionNotifyService.notifyLosers(auction);

        // ====== DISABLE AUTO BID ======
        autoBidService.disableAuctionAutoBids(auction.getAuctionId());

        auction.setPaid(true);

        auctionRepository.save(auction);
    }

    private Money calculateDiscountPrice(UserEntity user, Money originalPrice){

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalPrice.percentage(discountPercent);

        return originalPrice.subtract(discount);
    }
}
