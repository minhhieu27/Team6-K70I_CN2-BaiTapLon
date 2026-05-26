package com.app.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.auction.AuctionSearchRequest;
import com.app.dto.request.auction.CreateAuctionRequest;
import com.app.dto.response.auction.AuctionResponse;
import com.app.dto.response.message.MessageResponse;
import com.app.service.auction.AuctionFollowService;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionQuerryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auctions")
public class AuctionController {
    
    @Autowired
    private AuctionManagementService auctionManagementService;

    @Autowired
    private AuctionQuerryService auctionQuerryService;

    @Autowired
    private AuctionFollowService auctionFollowService;

    // ====== CREATE AUCTION ======
    @PostMapping
    public AuctionResponse create(@Valid @RequestBody CreateAuctionRequest req, Principal principal){

        return auctionManagementService.createAuction(req, principal.getName());
    }

    @GetMapping
    public List<AuctionResponse> getAll(){
        return auctionQuerryService.getAll();
    }
    

    @GetMapping ("/{auctionId}")
    public AuctionResponse get(@PathVariable String auctionId){
        return auctionQuerryService.getByAuctionId(auctionId);
    }

    @PostMapping("/{auctionId}/follow")
    public MessageResponse followAuction(@PathVariable String auctionId, Authentication authentication){

        return auctionFollowService.followAuction(auctionId, authentication.getName());
    }

    @PostMapping("/{auctionId}/unfollow")
    public MessageResponse unfollowAuction(@PathVariable String auctionId, Authentication authentication){

        return auctionFollowService.unfollowAuction(auctionId, authentication.getName());
    }

    @PostMapping("/search")
    public Page<AuctionResponse> search(@RequestBody AuctionSearchRequest req){

        return auctionQuerryService.search(req);
    }
}
