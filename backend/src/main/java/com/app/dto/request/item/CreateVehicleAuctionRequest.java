package com.app.dto.request.item;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleAuctionRequest extends CreateItemRequest {
    
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String fuelType;

    @NotBlank 
    private String color;

    @NotNull
    private Long mileage;

    @NotNull
    private Integer year;
}
