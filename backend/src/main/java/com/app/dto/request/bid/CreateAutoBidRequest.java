package com.app.dto.request.bid;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAutoBidRequest {
    
    @NotBlank
    private String auctionId;

    @NotNull
    @Positive
    private BigDecimal maxAmount;
}
