package com.app.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.common.enums.AuctionStatus;
import com.app.common.tool.DateTimeUtil;
import com.app.entity.AuctionEntity;
import com.app.entity.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.service.AuctionService;
import com.app.service.NotificationService;

@Component
public class AuctionScheduler {
    
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private NotificationService notificationService;

    private static final int EXTEND_THRESHOLD = 30;
    private static final int COOLDOWN_TIME = 30;

    @Scheduled (fixedRate = 5000)
    public void updateAuctions(){

        List<AuctionEntity> auctions = auctionRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (AuctionEntity auction : auctions){

            // ====== OPEN ======
            if (auction.getStatus() == AuctionStatus.SCHEDULED && now.isAfter(auction.getStartTime())){

                auction.setStatus(AuctionStatus.OPEN);

                auctionRepository.save(auction);

                continue;
            }

            // ====== FINISH ======
            if (auction.getStatus() == AuctionStatus.OPEN && now.isAfter(auction.getEndTime())){

                auctionService.finishAuction(auction.getAuctionId());
            }
        }
    }

    @Scheduled (fixedRate = 5000)
    public void notifyEndingSoon(){

        List<AuctionEntity> auctions = auctionRepository.findAll();

        for (AuctionEntity auction : auctions){

            if (auction.getStatus() != AuctionStatus.OPEN){
                continue;
            }

            long seconds = DateTimeUtil.secondLeft(auction.getEndTime());

            // Cooldown chống spam notify
            boolean canNotify = DateTimeUtil.passSeconds(auction.getLastEndingNotificationAt(), COOLDOWN_TIME);

            if (seconds > EXTEND_THRESHOLD){
                continue;
            }

            if (seconds <= 0){
                continue;
            }

            if (!canNotify){
                continue;
            }

            for (UserEntity follower : auction.getFollowers()){

                notificationService.notifyUser(follower, "Đấu giá sắp kết thúc: " + auction.getTitle() + " còn " + seconds + " giây");
            }

            auction.setLastEndingNotificationAt(LocalDateTime.now());

            auctionRepository.save(auction);
        
        }
    }
}
