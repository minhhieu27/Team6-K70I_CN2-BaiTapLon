package com.app.dto.response.wallet;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {

    private BigDecimal balance;
    private String userId;
    private BigDecimal lockedBalance;
    private BigDecimal totalSpent;
}
