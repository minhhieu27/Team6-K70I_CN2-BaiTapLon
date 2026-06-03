package com.app.dto.response.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.app.common.enums.AuctionStatus;
import com.app.dto.response.item.ItemResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionResponse {
    
    private String auctionId;

    private String title;

    private ItemResponse item;

    private List<AuctionImageResponse> images;

    private BigDecimal currentPrice;

    private AuctionStatus status;
    
    private String sellerId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long remainingSeconds;
}
