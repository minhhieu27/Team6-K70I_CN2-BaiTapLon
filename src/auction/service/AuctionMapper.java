package auction.service;

import auction.model.*;
import auction.view.AuctionView;

import java.util.*;

public class AuctionMapper {
    public AuctionView toView(Auction auction){
        List<Bid> bids = auction.getBidHistory();

        String highestBidder = "None";
        BigDecimal currentPrice = auction.getCurrentPrice();

        if (!bids.isEmpty()){
            Bid lastBid = bids.get(bids.size() - 1);
            highestBidder = lastBid.getBidder();
        }

        return new AuctionView(auction.getItemName(), currentPrice, highestBidder);
    }
}
