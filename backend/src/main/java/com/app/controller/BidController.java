package com.app.controller;

import com.app.service.bid.BidQuerryService;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.bid.CreateBidRequest;
import com.app.dto.response.bid.BidResponse;
import com.app.service.bid.BidService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
    
    private final BidQuerryService bidQuerryService;

    private final BidService bidService;

    @PostMapping("/{auctionId}")
    public BidResponse placeBid(@Valid @RequestBody CreateBidRequest req, Principal principal){
        
        return bidService.placeBid(req.getAuctionId(), new Money(req.getAmount()), principal.getName());
    }

    @GetMapping("/{auctionId}/history")
    public Page<BidResponse> getBidHistory(@Valid @RequestBody CreateBidRequest req, 
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){

        return  bidQuerryService.getAuctionBidHistory(req.getAuctionId(), page, size);
    }
}
