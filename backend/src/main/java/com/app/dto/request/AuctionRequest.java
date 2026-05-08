package com.app.dto.request;

import java.math.BigDecimal;

public class AuctionRequest {
    private String title;
    private BigDecimal startPrice;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public BigDecimal getStartPrice(){
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice){
        this.startPrice = startPrice;
    }
}
