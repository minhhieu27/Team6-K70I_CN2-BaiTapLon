package com.app.dto.view;

import java.math.BigDecimal;

public class AuctionView {
    private final String itemName;
    private final BigDecimal currentPrice;
    private final String highestBidder;

    public AuctionView(String itemName, BigDecimal currentPrice, String highestBidder){
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
    }

    public String getItemName(){
        return itemName;
    }

    public BigDecimal getCurrentPrice(){
        return currentPrice;
    }

    public String getHighestBidder(){
        return highestBidder;
    }
}
