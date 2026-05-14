package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.AuctionEntity;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long>{
    Optional<AuctionEntity> findByAuctionId(String auctionId);
}
