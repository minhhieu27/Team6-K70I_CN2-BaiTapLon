package com.app.dto.request.auction;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFashionAuctionRequest extends CreateAuctionRequest {
    
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String size;

    @NotBlank
    private String color;

    @NotBlank 
    private String material;
}
