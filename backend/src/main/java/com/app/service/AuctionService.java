package com.app.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.common.money.Money;
import com.app.common.tool.DateTimeUtil;
import com.app.common.tool.IDGenerator;
import com.app.dto.request.CreateAuctionRequest;
import com.app.dto.response.AuctionResponse;
import com.app.dto.response.MessageResponse;
import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import com.app.exception.auction.AuctionClosedException;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.exception.auction.NoUserJoinAuctionException;
import com.app.exception.user.UserNotFoundException;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.repository.UserRepository;

@Service
public class AuctionService {

    private final AutoBidService autoBidService;

    private final WalletService walletService;

    private static final int EXTEND_THRESHOLD = 30;
    private static final int EXTEND_TIME = 60;
    private static final int AUCTION_TIME = 30;
    private static final int SCHEDULED_TIME = 15;
    
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private NotificationService notificationService;

    AuctionService(WalletService walletService, AutoBidService autoBidService) {
        this.walletService = walletService;
        this.autoBidService = autoBidService;
    }

    // ====== CREATE AUCTION ======
    public AuctionResponse createAuction(CreateAuctionRequest req, String sellerId){

        UserEntity seller = userRepository.findByUserId(sellerId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        AuctionEntity auction = new AuctionEntity();

        LocalDateTime start = LocalDateTime.now().plusSeconds(SCHEDULED_TIME);

        AuctionStatus status = start.isAfter(LocalDateTime.now()) ? AuctionStatus.SCHEDULED : AuctionStatus.OPEN;

        auction.setAuctionId(IDGenerator.generateAuctionId());

        auction.setTitle(req.getTitle());
        auction.setItemName(req.getItemName());
        auction.setDescription(req.getDescription());
        auction.setStartPrice(new Money(req.getStartPrice()));
        auction.setCurrentPrice(new Money(req.getStartPrice()));
        auction.setSeller(seller);

        auction.setStartTime(start);
        auction.setEndTime(start.plusMinutes(AUCTION_TIME));

        auction.setStatus(status);

        AuctionEntity saveAuction = auctionRepository.save(auction);

        notificationService.notifyAllUsers("Có phiên đấu giá mới: " + saveAuction.getTitle());

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

        // Tính thời gian còn lại
        long secondLeft = DateTimeUtil.secondLeft(auction.getEndTime());

        if (secondLeft <= EXTEND_THRESHOLD){
            auction.setEndTime(auction.getEndTime().plusSeconds(EXTEND_TIME));

            // Reset notify về null
            auction.setLastEndingNotificationAt(null);

            auctionRepository.save(auction);

            notifyAuctionExtend(auction);
        }
    }

    private void notifyAuctionExtend(AuctionEntity auction){

        for (UserEntity follower : auction.getFollowers()) {
            notificationService.notifyUser(follower, "Phiên đấu giá " + auction.getTitle() + " đã được gia hạn thêm " + EXTEND_TIME + " giây do có bid mới");
        }
    }

    // ====== FOLLOW AUCTION ======
    public MessageResponse followAuction(String auctionId, String userId){

        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        auction.addFollower(user);

        return new MessageResponse("Đã theo dõi phiên đấu giá");
    }

    // ====== HIGHEST BIDDER ======
    public String getHighestBidder(String auctionId){

        AuctionEntity auction = getEntityByAuctionId(auctionId);

        if (auction.getHighestBidder() == null){
            return null;
        }

        return auction.getHighestBidder().getUserId();
    }

    // ====== CURRENT PRICE ======
    public Money getCurrentPrice(String auctionId){
        AuctionEntity auction = getEntityByAuctionId(auctionId);

        return auction.getCurrentPrice();
    }

    // ====== FINISH AUCTION ======
    public void finishAuction(String auctionId){

        AuctionEntity auction = getEntityByAuctionId(auctionId);

        updateStatus(auction);

        if (auction.getStatus() != AuctionStatus.FINISHED){
            throw new AuctionClosedException("Đấu giá chưa kết thúc");
        }

        if (auction.isPaid()) {
            return;
        }

        auction.setStatus(AuctionStatus.FINISHED);

        // ====== WINNER ======
        UserEntity winner = auction.getHighestBidder();

        if (winner == null){
            throw new NoUserJoinAuctionException("Không có người tham gia đấu giá");
        }

        UserEntity seller = auction.getSeller();

        Money amount = auction.getCurrentPrice();

        // ====== PAYMENT ======
        walletService.paySeller(winner, seller, amount);

        // ====== NOTIFY WINNER ====== 
        notificationService.notifyUser(winner, "Bạn đã thắng đấu giá: " + auction.getTitle());

        // ====== NOTIFY LOSERS ======
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

        // ====== DISABLE AUTO BID ======
        autoBidService.disableAuctionAutoBids(auction.getAuctionId());

        auction.setPaid(true);

        auctionRepository.save(auction);
    }

    // ====== SAVE ======
    public AuctionEntity save(AuctionEntity auction){
        
        return auctionRepository.save(auction);
    }
}