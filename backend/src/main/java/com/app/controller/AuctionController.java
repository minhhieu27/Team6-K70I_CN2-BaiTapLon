package com.app.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.CreateAuctionRequest;
import com.app.dto.response.AuctionResponse;
import com.app.dto.response.MessageResponse;
import com.app.service.AuctionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auctions")
public class AuctionController {
    
    @Autowired
    private AuctionService auctionService;

    // ====== CREATE AUCTION ======
    @PostMapping
    public AuctionResponse create(@Valid @RequestBody CreateAuctionRequest req, Principal principal){

        return auctionService.createAuction(req, principal.getName());
    }

    @GetMapping
    public List<AuctionResponse> getAll(){
        return auctionService.getAll();
    }
    

    @GetMapping ("/{auctionId}")
    public AuctionResponse get(@PathVariable String auctionId){
        return auctionService.getByAuctionId(auctionId);
    }

    @PostMapping("/{auctionId}/follow")
    public MessageResponse followAuction(@PathVariable String auctionId, Authentication authentication){

        return auctionService.followAuction(auctionId, authentication.getName());
    }

    @PostMapping("/{auctionId}/finish")
    public MessageResponse finishAuction(@PathVariable String auctionId){

        auctionService.finishAuction(auctionId);

        return new MessageResponse("Đấu giá thành công");
    }
}
