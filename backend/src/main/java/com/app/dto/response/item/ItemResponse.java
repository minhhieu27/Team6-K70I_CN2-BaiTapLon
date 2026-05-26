package com.app.dto.response.item;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    
    private String itemType;

    private String itemName;

    private String description;

    private BigDecimal startPrice;
}
