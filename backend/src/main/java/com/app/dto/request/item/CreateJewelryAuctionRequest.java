package com.app.dto.request.item;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJewelryAuctionRequest extends CreateItemRequest {
    
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String material;

    @NotNull
    private Double weight;
}
