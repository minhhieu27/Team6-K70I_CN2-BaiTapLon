package com.app.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.AuctionRequest;
import com.app.entity.Auction;
import com.app.service.AuctionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auctions")
public class AuctionController {
    
    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public Auction create(@RequestBody AuctionRequest req, Principal principal){
        return auctionService.createAuction(req.getTitle().toString(),
                new BigDecimal(req.getStartPrice().toString()),
                principal.getName());
    }

    @GetMapping
    public List<Auction> getAll(){
        return auctionService.getAll();
    }
    

    @GetMapping ("/{auctionId}")
    public Auction get(@PathVariable String auctionId){
        return auctionService.getByAuctionId(auctionId);
    }
}
