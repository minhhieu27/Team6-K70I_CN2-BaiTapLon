package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;
import java.util.List;

import java.util.Optional;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    Optional<BidEntity> findTopByUserAndAuctionOrderByAmount_ValueDesc(UserEntity user, AuctionEntity auction);
    List<BidEntity> findByAuctionOrderByCreateBidAtDesc(AuctionEntity auction);
    Optional<BidEntity>findTopByAuctionOrderByAmount_ValueDesc(AuctionEntity auction);

}
