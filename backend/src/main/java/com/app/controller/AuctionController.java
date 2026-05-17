package com.app.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.AuctionRequest;
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
    public AuctionResponse create(@Valid @RequestBody AuctionRequest req, Principal principal){

        return auctionService.createAuction(req.getTitle(), req.getItemName(), req.getDescription(), new Money(req.getStartPrice()), principal.getName());
    }

    @GetMapping
    public List<AuctionResponse> getAll(){
        return auctionService.getAll();
    }
    

    @GetMapping ("/{auctionId}")
    public AuctionResponse get(@PathVariable String auctionId){
        return auctionService.getByAuctionId(auctionId);
    }

    @PostMapping("/{auctionId}/finish")
    public MessageResponse finishAuction(@PathVariable String auctionId){

        auctionService.finishAuction(auctionId);

        return new MessageResponse("Đấu giá thành công");
    }
}
