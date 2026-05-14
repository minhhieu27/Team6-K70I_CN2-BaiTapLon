package com.app.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WithdrawRequest {
    
    @NotNull (message = "Số tiền không được để trống")
    @Positive (message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;
}
