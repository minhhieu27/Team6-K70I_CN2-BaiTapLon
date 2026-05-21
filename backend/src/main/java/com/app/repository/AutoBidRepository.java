package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.AutoBidEntity;

public interface AutoBidRepository extends JpaRepository<AutoBidEntity, String> {
    
    // Mỗi user chỉ được tạo 1 autoBid cho 1 auction
    Optional<AutoBidEntity> findByUser_UserIdAndAuction_AuctionId(String userId, String auctionId);

    // Lấy toàn bộ autoBid đang hoạt động của auction
    List<AutoBidEntity> findByAuction_AuctionIdAndActiveTrue(String auctionId);

    // Lấy toàn bộ autoBid của 1 user
    List<AutoBidEntity> findByUser_UserId(String userId);
}
