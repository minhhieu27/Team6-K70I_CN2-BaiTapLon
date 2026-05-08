package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long>{
    Optional<Auction> findByAuctionId(String auctionId);
}
