package com.app.service.bid;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.money.Money;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.AutoBidEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.exception.auction.AutoBidAlreadyExistsException;
import com.app.exception.auction.AutoBidNotFoundException;
import com.app.exception.user.UserNotFoundException;
import com.app.repository.AuctionRepository;
import com.app.repository.AutoBidRepository;
import com.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.app.common.strategy.FixedBidStrategy;
import com.app.dto.response.message.MessageResponse;

@Service
@RequiredArgsConstructor
public class AutoBidService {
    
    @Autowired
    @Lazy
    private BidCoreService bidCoreService;

    private final AutoBidRepository autoBidRepository;

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    private final FixedBidStrategy fixedBidStrategy;

    // ====== CREATE AUTO BID ======
    public MessageResponse createAutoBid(String userId, String auctionId, Money maxAmount){
        
        Optional<AutoBidEntity> existsing = autoBidRepository.findByUser_UserIdAndAuction_AuctionId(userId, auctionId);

        if (existsing.isPresent()) {
            throw new AutoBidAlreadyExistsException("Đã thiết lập auto bid");
        }

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy auction"));

        AutoBidEntity autoBid = new AutoBidEntity(user, auction, maxAmount);

        autoBidRepository.save(autoBid);

        // Trigger ngay nếu user chưa phải highest bidder
        if (auction.getHighestBidder() == null || 
            !auction.getHighestBidder().getUserId().equals(userId)) {
            processAutoBid(auction);
        }

        return new MessageResponse("Tạo auto bid thành công");
    }

    // ====== DISABLE AUTO BID ======
    public MessageResponse disableAutoBid(String userId, String auctionId){

        AutoBidEntity autoBid = autoBidRepository.findByUser_UserIdAndAuction_AuctionId(userId, auctionId).orElseThrow(() -> new AutoBidNotFoundException("Không tìm thấy auto bid"));

        autoBid.deactivate();

        autoBidRepository.save(autoBid);

        return new MessageResponse("Auto bid đã tắt");
    }

    // ====== DISABLE AUCTION AUTO BID ======
    public void disableAuctionAutoBids(String auctionId){

        List<AutoBidEntity> autoBids = autoBidRepository.findByAuction_AuctionIdAndActiveTrue(auctionId);

        for (AutoBidEntity autoBid : autoBids){

            autoBid.deactivate();
        }

        autoBidRepository.saveAll(autoBids);
    }

    // ====== PROCESS AUTO BID ======
    @Transactional
    public void processAutoBid(AuctionEntity auction){

        List<AutoBidEntity> autoBids = autoBidRepository.findByAuction_AuctionIdAndActiveTrue(auction.getAuctionId());

        for (AutoBidEntity autoBid : autoBids){

            // Nếu user đang dẫn đầu thì bỏ qua
            if (auction.getHighestBidder() != null && auction.getHighestBidder().getUserId().equals(autoBid.getUser().getUserId())){
                continue;
            }

            // Tính bid tiếp theo
            Money nextBid = fixedBidStrategy.calculateMinBid(auction);

            // Xét vượt max auto bid thì bỏ qua
            if (nextBid.isGreaterThan(autoBid.getMaxAmount())){
                continue;
            }

            // Auto place bid
            AutoBidEntity freshAutoBid = autoBidRepository.findById(autoBid.getAutoBid()).orElse(null);

                if (freshAutoBid == null || freshAutoBid.getUser() == null) continue;

                bidCoreService.excecuteBid(auction.getAuctionId(), nextBid, freshAutoBid.getUser().getUserId());

            // Chỉ cho 1 auto bid chạy mỗi 1 lần process
            break;
        }
    }

    public List<AutoBidEntity> getUserAutoBids(String userId){

        return autoBidRepository.findByUser_UserId(userId);
    }
}
