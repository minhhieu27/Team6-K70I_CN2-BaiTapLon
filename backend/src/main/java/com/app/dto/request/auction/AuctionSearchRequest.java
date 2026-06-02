package com.app.dto.request.auction;

import java.math.BigDecimal;

import com.app.common.enums.AuctionStatus;
import com.app.common.enums.ItemType;

import lombok.Data;

@Data
public class AuctionSearchRequest {
    
    private String keyword;

    private ItemType itemType;

    private AuctionStatus auctionStatus;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String sellerId;

    private String auctionId;

    // ====== PAGINATION ======
    private Integer page = 0;

    private Integer size = 10;
}
