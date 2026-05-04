package auction.mapper;

import auction.model.*;
import auction.view.AuctionView;

public class AuctionMapper {

    public static AuctionView toView(Auction auction){
        String highestBidder = auction.getHighestBidder();

        if (highestBidder == null){
            highestBidder = "None";
        }
        Money currentPrice = auction.getCurrentPrice();

        return new AuctionView(auction.getItemName(), currentPrice, highestBidder);
    }
}
