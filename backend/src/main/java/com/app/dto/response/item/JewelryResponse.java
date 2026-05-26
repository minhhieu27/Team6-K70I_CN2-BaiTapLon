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
public class JewelryResponse extends ItemResponse {
    
    private String brand;

    private String model;

    private  String material;

    private Double weight;
}
