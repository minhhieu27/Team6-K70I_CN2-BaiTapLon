package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.AuctionEntity;
import com.app.entity.BidEntity;
import com.app.entity.UserEntity;

import java.util.Optional;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    Optional<BidEntity> findTopByBidderAndAuctionOrderByAmountDesc(UserEntity bidder, AuctionEntity auction);

    Optional<BidEntity>findTopByAuctionOrderByAmountDesc(AuctionEntity auction);

}
