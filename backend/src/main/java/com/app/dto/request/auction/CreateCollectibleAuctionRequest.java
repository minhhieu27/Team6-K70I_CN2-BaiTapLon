package com.app.dto.request.auction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectibleAuctionRequest extends CreateAuctionRequest {
    
    @NotBlank
    private String category;

    @NotBlank
    private String rarity;

    @NotNull
    private Integer productionYear;
}
