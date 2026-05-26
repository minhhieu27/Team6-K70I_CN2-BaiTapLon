package com.app.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.common.enums.AuctionStatus;
import com.app.dto.response.message.MessageResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.exception.user.UserNotFoundException;
import com.app.repository.AuctionRepository;
import com.app.repository.UserRepository;

@Service
public class AdminService {
    
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    public MessageResponse deleteAuction(String auctionId){

        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));

        auction.setStatus(AuctionStatus.DELETED);

        auctionRepository.save(auction);

        return new MessageResponse("Đã xóa phiên đấu giá");
    }

    public MessageResponse banUser(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        user.banAccount();

        userRepository.save(user);

        return new MessageResponse("Tài khoản đã bị ban");
    }
}
