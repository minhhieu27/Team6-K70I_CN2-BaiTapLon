package com.app.dto.response.auction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionImageResponse {
    
    private String imageId;

    private String imageUrl;

    private Integer displayOrder;
}
