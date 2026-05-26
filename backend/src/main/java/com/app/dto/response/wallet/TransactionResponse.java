package com.app.dto.response.wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.common.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse{

    private Long transactionId;

    private BigDecimal amount;

    private TransactionType transactionType;

    private LocalDateTime createAt;
}
