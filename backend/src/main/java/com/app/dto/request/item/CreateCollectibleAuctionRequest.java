package com.app.dto.request.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectibleAuctionRequest extends CreateItemRequest {
    
    @NotBlank
    private String category;

    @NotBlank
    private String rarity;

    @NotNull
    private Integer productionYear;
}
