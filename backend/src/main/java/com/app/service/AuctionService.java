package com.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.domain.tool.IDGenerator;
import com.app.entity.Auction;
import com.app.repository.AuctionRepository;

@Service
public class AuctionService {
    
    @Autowired
    private AuctionRepository auctionRepository;

    public Auction createAuction(String title, BigDecimal startPrice, String userId){
        Auction auction = new Auction();
        auction.setTitle(title);
        auction.setAuctionId(IDGenerator.generateAuctionId());
        auction.setStartPrice(startPrice);
        auction.setCurrentPrice(startPrice);
        auction.setUserId(userId);
        auction.setStatus("OPEN");

        return auctionRepository.save(auction);
    }

    public List<Auction> getAll(){
        return auctionRepository.findAll();
    }

    public Auction getByAuctionId(String auctionId){
        return auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new RuntimeException("Không tìm thấy auction"));
    }

    public Auction save(Auction auction){
        return auctionRepository.save(auction);
    }
}
