package com.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.tool.DateTimeUtil;
import com.app.dto.response.AuctionResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;

@Service
public class AuctionService {

    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;
    
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionMapper auctionMapper;

    // ====== CREATE AUCTION ======
    public AuctionResponse createAuction(String title, String description,Money startPrice, String sellerId){

        UserEntity seller = userService.getEntityByUserId(sellerId);

        AuctionEntity auction = new AuctionEntity();

        auction.setTitle(title);
        auction.setDescription(description);
        auction.setStartPrice(startPrice);
        auction.setSeller(seller);

        AuctionEntity saveAuction = auctionRepository.save(auction);

        return auctionMapper.toResponse(saveAuction);
    }

    // ====== GET ALL ======
    public List<AuctionResponse> getAll(){
        return auctionMapper.toResponseList(auctionRepository.findAll());
    }

    // ====== GET BY ID ======
    public AuctionResponse getByAuctionId(String auctionId){
        return auctionMapper.toResponse(getEntityByAuctionId(auctionId));
    }

    // ====== GET ENTITY ======
    public AuctionEntity getEntityByAuctionId(String auctionId){

        return auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));
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

        long secondLeft = DateTimeUtil.secondLeft(auction.getEndTime());

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));
        }
    }

    // ====== HIGHEST BIDDER ======
    public String getHighestBidder(String auctionId){

        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));

        return bidRepository.findTopByAuctionOrderByAmountDesc(auction).map(bid -> bid.getUser().getUserId()).orElse(null);
    }

    // ====== CURRENT PRICE
    public Money getCurrentPrice(String auctionId){
        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));

        return bidRepository.findTopByAuctionOrderByAmountDesc(auction).map(BidEntity::getAmount).orElse(auction.getStartPrice());
    }

    // ====== SAVE ======
    public AuctionEntity save(AuctionEntity auction){
        
        return auctionRepository.save(auction);
    }
}