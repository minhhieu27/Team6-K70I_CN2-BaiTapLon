package com.app.dto.request;

import java.math.BigDecimal;

public class BidRequest {
    private String auctionId;
    private BigDecimal amount;

    public String getAuctionId() {return auctionId;}
    public void setAuctionId(String auctionId){this.auctionId = auctionId;}

    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}
}
