package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.BidResponse;
import com.app.entity.BidEntity;

@Component
public class BidMapper {
    
    public BidResponse toResponse(BidEntity bid){

        return BidResponse.builder()
                .bidId(bid.getId())
                .auctionId(bid.getAuction().getAuctionId())
                .amount(bid.getAmount().getAmount())
                .bidderId(bid.getUser().getUserId())
                .build();
    }
}
