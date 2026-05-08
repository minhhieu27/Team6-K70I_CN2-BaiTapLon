package com.app.controller;

import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.BidRequest;
import com.app.service.BidService;

@RestController
@RequestMapping("/bids")
public class BidController {
    
    @Autowired
    private BidService bidService;

    @PostMapping
    public String bid(@RequestBody BidRequest req, Principal principal){
        return bidService.placeBid(req.getAuctionId().toString(),
                new BigDecimal(req.getAmount().toString()),
                principal.getName());
    }
}
