package com.app.domain.mapper;

import com.app.domain.model.*;
import com.app.domain.view.AuctionView;

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
