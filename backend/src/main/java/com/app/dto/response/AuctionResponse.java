package com.app.dto.response;

import java.math.BigDecimal;

import com.app.common.enums.AuctionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionResponse {
    
    private String auctionId;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private AuctionStatus status;
    private String sellerId;
}
