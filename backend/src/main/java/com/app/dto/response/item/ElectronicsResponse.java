package com.app.dto.response.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
public class ElectronicsResponse extends ItemResponse {
    
    private String brand;

    private String model;

    private String color;

    private String storage;

    private String conditionStatus;

    private Integer warrantyMonths;
}
