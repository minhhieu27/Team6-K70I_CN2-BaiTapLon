package com.app.controller;

import com.app.service.bid.BidQuerryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.bid.BidResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
    
    private final BidQuerryService bidQuerryService;

    @GetMapping("/{auctionId}/history")
    public Page<BidResponse> getBidHistory(@PathVariable String auctionId, 
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){

        return bidQuerryService.getAuctionBidHistory(auctionId, page, size);
    }
}
