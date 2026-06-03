package com.app.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.enums.AuctionStatus;
import com.app.common.tool.DateTimeUtil;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.repository.AuctionRepository;
import com.app.service.auction.AuctionManagementService;
import com.app.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {
    
    private final AuctionRepository auctionRepository;

    private final AuctionManagementService auctionManagementService;

    private final NotificationService notificationService;

    private static final int EXTEND_THRESHOLD = 30;
    private static final int COOLDOWN_TIME = 30;

    @Scheduled (fixedRate = 5000)
    public void updateAuctions(){

        List<AuctionEntity> auctions = auctionRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (AuctionEntity auction : auctions){

            // ====== OPEN ======
            if (auction.getStatus() == AuctionStatus.SCHEDULED && !now.isBefore(auction.getStartTime())){

                auction.setStatus(AuctionStatus.OPEN);

                auctionRepository.save(auction);

                continue;
            }

            // ====== FINISH ======
            if (auction.getStatus() == AuctionStatus.OPEN && now.isAfter(auction.getEndTime())){

               auctionManagementService.finishAuction(auction.getAuctionId());
            }
        }
    }

    @Transactional
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