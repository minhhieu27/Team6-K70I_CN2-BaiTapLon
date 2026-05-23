package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.CreateAutoBidRequest;
import com.app.dto.response.MessageResponse;
import com.app.entity.AutoBidEntity;
import com.app.service.AutoBidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/autobids")
public class AutoBidController {
    
    @Autowired
    private AutoBidService autoBidService;

    // ====== CREATE AUTO BID ======
    @PostMapping
    public MessageResponse createAutoBid(@Valid @RequestBody CreateAutoBidRequest req, Authentication authentication){

        return autoBidService.createAutoBid(authentication.getName(), req.getAuctionId(), new Money(req.getMaxAmount()));
    }

    // ====== DISABLE ======
    @DeleteMapping("/{auctionId}")
    public MessageResponse disableAutoBid(@PathVariable String auctionid, Authentication authentication){

        return autoBidService.disableAutoBid(authentication.getName(), auctionid);
    }

    // ====== MY AUTO BIDS ======
    @GetMapping("/me")
    public List<AutoBidEntity> myAutoBids(Authentication authentication){

        return autoBidService.getUserAutoBids(authentication.getName());
    }
}
