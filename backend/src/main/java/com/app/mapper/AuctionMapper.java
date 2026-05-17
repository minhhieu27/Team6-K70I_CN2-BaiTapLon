package com.app.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.dto.response.AuctionResponse;
import com.app.entity.AuctionEntity;

@Component
public class AuctionMapper {

    public AuctionResponse toResponse(AuctionEntity auction){

        return AuctionResponse.builder()
                .auctionId(auction.getAuctionId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .currentPrice(auction.getCurrentPrice().getValue())
                .status(auction.getStatus())
                .sellerId(auction.getSeller().getUserId())
                .build();
    }

    public List<AuctionResponse> toResponseList(List<AuctionEntity> auctions){
        
        return auctions.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
