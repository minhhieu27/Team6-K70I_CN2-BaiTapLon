package com.app.dto.request.auction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateElectronicsAuctionRequest extends CreateAuctionRequest {
    
    @NotBlank
    private String brand;

    @NotBlank
    private String model;
    
    @NotBlank
    private String conditionStatus;

    @NotNull
    private Integer warrantyMonths;

    @NotBlank
    private String storage;

    @NotBlank
    private String color;
}
