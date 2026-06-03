package com.app.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.common.tool.DateTimeUtil;
import com.app.dto.response.auction.AuctionImageResponse;
import com.app.dto.response.auction.AuctionResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.entity.auction.AuctionImageEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionMapper {

    private final ItemMapper itemMapper;

    public AuctionResponse toResponse(AuctionEntity auction){

        return AuctionResponse.builder()
                .auctionId(auction.getAuctionId())
                .title(auction.getTitle())
                .item(itemMapper.toResponse(auction.getItem()))
                .images(auction.getImages().stream().map(this::toImageResponse).toList())
                .currentPrice(auction.getCurrentPrice().getValue())
                .status(auction.getStatus())
                .sellerId(auction.getSeller().getUserId())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .remainingSeconds(DateTimeUtil.secondLeft(auction.getEndTime()))
                .build();
    }

    private AuctionImageResponse toImageResponse(AuctionImageEntity image){

        return AuctionImageResponse.builder()
                                .imageId(image.getImageId())
                                .imageUrl(image.getImageUrl())
                                .displayOrder(image.getDisplayOrder())
                                .build();
    }

    public List<AuctionResponse> toResponseList(List<AuctionEntity> auctions){
        
        return auctions.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
