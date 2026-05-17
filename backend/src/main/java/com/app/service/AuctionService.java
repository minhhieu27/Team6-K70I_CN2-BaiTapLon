package com.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.tool.DateTimeUtil;
import com.app.common.tool.IDGenerator;
import com.app.dto.response.AuctionResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.exception.user.UserNotFoundException;
import com.app.exception.wallet.AuctionAlreadyPaidException;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.BidRepository;
import com.app.repository.UserRepository;

@Service
public class AuctionService {

    private final WalletService walletService;
    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;
    private static final int AUCTION_TIME = 30;
    
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionMapper auctionMapper;

    AuctionService(WalletService walletService) {
        this.walletService = walletService;
    }

    // ====== CREATE AUCTION ======
    public AuctionResponse createAuction(String title, String itemName, String description, Money startPrice, String sellerId){

        UserEntity seller = userRepository.findByUserId(sellerId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        AuctionEntity auction = new AuctionEntity();

        LocalDateTime start = LocalDateTime.now();

        auction.setAuctionId(IDGenerator.generateAuctionId());

        auction.setTitle(title);
        auction.setItemName(itemName);
        auction.setDescription(description);
        auction.setStartPrice(startPrice);
        auction.setCurrentPrice(startPrice);
        auction.setSeller(seller);

        auction.setStartTime(start);
        auction.setEndTime(start.plusSeconds(AUCTION_TIME));

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

        AuctionEntity auction = getEntityByAuctionId(auctionId);

        return bidRepository.findTopByAuctionOrderByAmount_ValueDesc(auction).map(bid -> bid.getUser().getUserId()).orElse(null);
    }

    // ====== CURRENT PRICE ======
    public Money getCurrentPrice(String auctionId){
        AuctionEntity auction = getEntityByAuctionId(auctionId);

        return bidRepository.findTopByAuctionOrderByAmount_ValueDesc(auction).map(BidEntity::getAmount).orElse(auction.getStartPrice());
    }

    // ====== FINISH AUCTION ======
    public void finishAuction(String auctionId){

        AuctionEntity auction = getEntityByAuctionId(auctionId);

        updateStatus(auction);

        if (auction.getStatus() != AuctionStatus.FINISHED){
            throw new AuctionClosedException("Đấu giá chưa kết thúc");
        }

        if (auction.isPaid()) {
            throw new AuctionAlreadyPaidException("Đã thanh toán");
        }

        BidEntity highestBid = bidRepository.findTopByAuctionOrderByAmount_ValueDesc(auction).orElse(null);

        UserEntity winner = highestBid.getUser();

        UserEntity seller = auction.getSeller();

        Money amount = highestBid.getAmount();

        walletService.paySeller(winner, seller, amount);

        auctionRepository.save(auction);
    }

    // ====== SAVE ======
    public AuctionEntity save(AuctionEntity auction){
        
        return auctionRepository.save(auction);
    }
}