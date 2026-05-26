package com.app.dto.request.auction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJewelryAuctionRequest extends CreateAuctionRequest {
    
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String material;

    @NotBlank
    private String gemStone;

    @NotBlank
    private String gemStoneColor;

    @NotNull
    private Double weight;
}
