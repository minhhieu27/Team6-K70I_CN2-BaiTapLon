package com.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.auction.AuctionEntity;
import com.app.entity.bid.BidEntity;
import com.app.entity.user.UserEntity;

import java.util.Optional;
import java.util.List;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    Optional<BidEntity> findTopByUserAndAuctionOrderByAmount_ValueDesc(UserEntity user, AuctionEntity auction);

    List<BidEntity> findByAuctionOrderByCreateBidAtDesc(AuctionEntity auction);

    Page<BidEntity> findByAuction_AuctionIdOrderByCreateBidAtDesc(String auctionId, Pageable pageable);
}
