package auction.view;

import auction.model.Money;
import auction.tool.FormatUtil;

public class AuctionView {
    private final String itemName;
    private final Money currentPrice;
    private final String highestBidder;

    public AuctionView(String itemName, Money currentPrice, String highestBidder){
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
    }

    public String getItemName(){
        return itemName;
    }

    public Money getCurrentPrice(){
        return currentPrice;
    }

    public String getHighestBidder(){
        return highestBidder;
    }

    @Override
    public String toString(){
        return "Item: " + itemName + 
                "\nHisghest Bidder: " + highestBidder +
                "\nCurrent Price: " + FormatUtil.formatMoney(currentPrice);
    }
}
