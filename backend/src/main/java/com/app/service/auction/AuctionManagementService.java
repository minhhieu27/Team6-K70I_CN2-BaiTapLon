package com.app.service.auction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.tool.DateTimeUtil;
import com.app.common.tool.IDGenerator;
import com.app.dto.request.auction.CreateAuctionRequest;
import com.app.dto.response.auction.AuctionResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.auction.AuctionImageEntity;
import com.app.entity.item.ItemEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.user.UserNotFoundException;
import com.app.factory.ItemFactoryManager;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionManagementService {
    
    private final AuctionPaymentService auctionPaymentService;

    private final AuctionRepository auctionRepository;

    private final UserRepository userRepository;

    private final AuctionMapper auctionMapper;

    private final ItemFactoryManager itemFactoryManager;

    private final AuctionQuerryService auctionQuerryService;

    private final AuctionNotifyService auctionNotifyService;

    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;
    private static final int AUCTION_TIME = 30;
    private static final int SCHEDULED_TIME = 15;

    // ====== CREATE AUCTION ======
    public AuctionResponse createAuction(CreateAuctionRequest req, String sellerId){

        // ====== CREATE ITEM ======
        ItemEntity item = itemFactoryManager.createItem(req.getItem().getItemType(), req.getItem());

        // ====== FIND SELLER ======
        UserEntity seller = userRepository.findByUserId(sellerId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        // ====== START TIME ======
        LocalDateTime start = LocalDateTime.now().plusSeconds(SCHEDULED_TIME);

        // ====== STATUS ======
        AuctionStatus status = start.isAfter(LocalDateTime.now()) ? AuctionStatus.SCHEDULED : AuctionStatus.OPEN;

        // ====== CREATE AUCTION ======
        AuctionEntity auction = new AuctionEntity();

        auction.setAuctionId(IDGenerator.generateAuctionId());

        auction.setTitle(req.getTitle());

        auction.setItem(item);

        auction.setCurrentPrice(new Money(req.getItem().getStartPrice()));

        addImages(auction, req.getImageUrls());

        auction.setSeller(seller);

        auction.setStartTime(start);
        auction.setEndTime(start.plusMinutes(AUCTION_TIME));

        auction.setStatus(status);

        // ====== SAVE ======
        AuctionEntity saveAuction = auctionRepository.save(auction);

        auctionNotifyService.notifyNewAuction(saveAuction);

        return auctionMapper.toResponse(saveAuction);
    }

    // ====== IMAGES ======
    private void addImages(AuctionEntity auction, List<String> imageUrls){

        int index = 0;

        for (String url : imageUrls){

            AuctionImageEntity image = AuctionImageEntity.builder()
                                                        .imageUrl(url)
                                                        .displayOrder(index++)
                                                        .build();
            auction.addImage(image);
        }
    }

    // ====== UPDATE STATUS ======
    public void updateStatus(AuctionEntity auction) {

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())){
            auction.setStatus(AuctionStatus.SCHEDULED);

        } else if (now.isAfter(auction.getEndTime())){
            auction.setStatus(AuctionStatus.FINISHED);

        } else {
            auction.setStatus(AuctionStatus.OPEN);
        }
    }

    // ====== EXTEND TIME ======
    public void extendTime(AuctionEntity auction){

        // Tính thời gian còn lại
        long secondLeft = DateTimeUtil.secondLeft(auction.getEndTime());

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));

            // Reset notify về null
            auction.setLastEndingNotificationAt(null);

            auctionRepository.save(auction);

            auctionNotifyService.notifyAuctionExtend(auction);
        }
    }

    // ====== FINISH AUCTION ======
    @Transactional
    public void finishAuction(String auctionId){

        AuctionEntity auction = auctionQuerryService.getEntityByAuctionId(auctionId);

        updateStatus(auction);

        if (auction.getStatus() != AuctionStatus.FINISHED){
            return;
        }

        auctionPaymentService.settleAuction(auction);
    }
}
