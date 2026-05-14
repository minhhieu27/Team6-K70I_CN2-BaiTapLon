package com.app.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidResponse {
    private Long bidId;

    private String auctionId;

    private String bidderId;

    private BigDecimal amount;
}
