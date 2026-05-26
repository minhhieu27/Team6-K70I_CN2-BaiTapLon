package com.app.service.auction;

import org.springframework.stereotype.Service;

import com.app.dto.response.message.MessageResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.user.UserNotFoundException;
import com.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionFollowService {
    
    private final AuctionQuerryService auctionQuerryService;

    private final UserRepository userRepository;

    // ====== FOLLOW ======
    public MessageResponse followAuction(String auctionId, String userId){

        AuctionEntity auction = auctionQuerryService.getEntityByAuctionId(auctionId);

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        auction.addFollower(user);

        return new MessageResponse("Đã theo dõi phiên đấu giá");
    }

    // ====== UNFOLLOW ======
    public MessageResponse unfollowAuction(String auctionId, String userId){

        AuctionEntity auction = auctionQuerryService.getEntityByAuctionId(auctionId);

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        auction.removeFollower(user);

        return new MessageResponse("Đã bỏ theo dõi phiên đấu giá");
    }
}
