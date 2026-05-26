package com.app.dto.request.auction;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArtAuctionRequest extends CreateAuctionRequest {
    
    @NotBlank
    private String artist;

    @NotBlank
    private String style;
}
