package com.app.service.auction;

import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.service.bid.AutoBidService;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;


@Service
public class AuctionPaymentService {
    
    private final UserRepository userRepository;

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

    AuctionPaymentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ====== SETTLE ======
    public void settleAuction(AuctionEntity auction){

        if (auction.isPaid()){
            return;
        }

        // ====== WINNER ======
        UserEntity winner = auction.getHighestBidder();

        if (winner == null) {

            auction.setStatus(AuctionStatus.FAILED);

            auctionRepository.save(auction);

            return;
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

        // Cập nhật totalSpent và VIP level cho winner
        winner.setTotalSpent(winner.getTotalSpent() == null ? finalAmount.getValue() : winner.getTotalSpent().add(finalAmount.getValue()));

        winner.upgradeVIP();

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

        userRepository.save(winner);

        auctionRepository.save(auction);
    }

    private Money calculateDiscountPrice(UserEntity user, Money originalPrice){

        int discountPercent = user.getVipLevel().getDiscountPercent();

        Money discount = originalPrice.percentage(discountPercent);

        return originalPrice.subtract(discount);
    }
}
