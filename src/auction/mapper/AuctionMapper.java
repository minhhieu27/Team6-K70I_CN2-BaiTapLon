package auction.mapper;

import auction.model.*;
import auction.view.AuctionView;

import java.util.*;

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
