package auction.view;

import java.math.BigDecimal;

public class AuctionView {
    private String itemName;
    private BigDecimal currentPrice;
    private String highestBidder;

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

    @Override
    public String toString(){
        return "Item: " + itemName + 
                "\nHisghest Bidder: " + highestBidder +
                "\nCurrent Price: " + currentPrice;
    }
}
