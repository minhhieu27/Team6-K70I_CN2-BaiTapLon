package com.app.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.CreateBidRequest;
import com.app.dto.response.BidResponse;
import com.app.service.BidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bids")
public class BidController {
    
    @Autowired
    private BidService bidService;

    @PostMapping
    public BidResponse bid(@Valid @RequestBody CreateBidRequest req, Principal principal){
        
        return bidService.placeBid(req.getAuctionId(), new Money(req.getAmount()), principal.getName());
    }
}
