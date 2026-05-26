package com.app.dto.request.bid;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateBidRequest {

    @NotBlank
    private String auctionId;

    @NotNull (message = "Số tiền đấu giá không được để trống")
    @Positive (message = "Số tiền đấu giá phải lớn hơn 0")
    private BigDecimal amount;

}
