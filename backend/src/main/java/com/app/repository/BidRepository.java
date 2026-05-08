package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Bid;
import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuction_AuctionId(String auctionId);
}
