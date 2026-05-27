package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.common.enums.AuctionStatus;
import com.app.entity.auction.AuctionEntity;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long>, JpaSpecificationExecutor<AuctionEntity>{
    Optional<AuctionEntity> findByAuctionId(String auctionId);

    List<AuctionEntity> findByStatus(AuctionStatus status);
}
