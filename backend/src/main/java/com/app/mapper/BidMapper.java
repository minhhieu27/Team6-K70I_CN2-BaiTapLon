package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.bid.BidResponse;
import com.app.entity.bid.BidEntity;

@Component
public class BidMapper {
    
    public BidResponse toResponse(BidEntity bid){

        return BidResponse.builder()
                .bidId(bid.getId())
                .auctionId(bid.getAuction().getAuctionId())
                .amount(bid.getAmount().getValue())
                .bidderId(bid.getUser().getUserId())
                .build();
    }
}
