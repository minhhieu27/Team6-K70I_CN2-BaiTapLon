package com.app.service.auction;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;
import com.app.entity.user.UserEntity;
import com.app.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionNotifyService {

    private final NotificationService notificationService;

    private static final int EXTEND_TIME = 60;

    // ====== NEW AUCTION ======
    public void notifyNewAuction(AuctionEntity auction){

        notificationService.notifyAllUsers("Có phiên đấu giá mới: " + auction.getTitle());
    }

    // ====== EXTEND TIME ======
    public void notifyAuctionExtend(AuctionEntity auction){

        for (UserEntity follower : auction.getFollowers()) {
            notificationService.notifyUser(follower, "Phiên đấu giá " + auction.getTitle() + " đã được gia hạn thêm " + EXTEND_TIME + " giây do có bid mới");
        }
    }

    // ====== NOTIFY NEW BID ======
    public void notifyNewBid(AuctionEntity auction, BidEntity bid){
        for(UserEntity follower : auction.getFollowers()){

            notificationService.notifyUser(follower, bid.getUser().getUserId() + "đã đấu giá " + bid.getAmount().getValue() + " cho " + auction.getTitle());
        }
    }

    // ====== NOTIFY AUCTION ENDED ======
    public void notifyAuctionFinished(AuctionEntity auction){

        for (UserEntity follower : auction.getFollowers()){

            notificationService.notifyUser(follower, "Phiên đấu giá " + auction.getTitle() + " đã kết thúc");
        }
    }

    // ====== NOTIFY WINNER ======
    public void notifyWinner(AuctionEntity auction){

        UserEntity winner = auction.getHighestBidder();

        if (winner == null){
            return;
        }

        notificationService.notifyUser(winner, "Bạn đã thắng đấu giá: " + auction.getTitle());
    }

    // ====== NOTIFY LOSERS ======
    public void notifyLosers(AuctionEntity auction){

        UserEntity winner = auction.getHighestBidder();

        Set<String> notified = new  HashSet<>(); 

        for (BidEntity bid : auction.getBidHistory()){

            UserEntity bidder = bid.getUser();

            if (bidder.getUserId().equals(winner.getUserId())){
                continue;
            }

            if (notified.contains(bidder.getUserId())){
                continue;
            }

            notificationService.notifyUser(bidder, "Bạn đã thua đấu giá: " + auction.getTitle());

            notified.add(bidder.getUserId());
        }
    }

    // ====== FOLLOW ======
    public void notifyFollowers(AuctionEntity auction, String message){

        for (UserEntity follower : auction.getFollowers()){

            notificationService.notifyUser(follower, message);
        }
    }
}
