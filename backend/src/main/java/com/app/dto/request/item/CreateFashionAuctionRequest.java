package com.app.dto.request.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFashionAuctionRequest extends CreateItemRequest {
    
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
