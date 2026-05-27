package com.app.dto.request.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArtAuctionRequest extends CreateItemRequest {
    
    @NotBlank
    private String artist;

    @NotBlank
    private String style;
}
