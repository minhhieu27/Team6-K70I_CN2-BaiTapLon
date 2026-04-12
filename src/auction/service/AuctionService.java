package auction.service;

import auction.exception.AuctionClosedException;
import auction.exception.InvalidBidException;
import auction.model.*;

public class AuctionService {
    private Auction auction;

    public AuctionService(Auction auction){
        this.auction = auction;
    }

    public void placeBid(String bidder, double amount){
        Bid bid = new Bid(bidder, amount);

        try {

            auction.placeBid(bid); // Xét coi bid có hợp lệ hay không
    
            System.out.println("Bid thanh cong!");
            System.out.println("Highest Bidder: " +auction.getHighestBidder());
            System.out.println("Current Price: " + auction.getCurrentPrice());
            
        } catch (InvalidBidException e) {
            System.out.println("Bid failed: Price lower than current price!");

        } catch (AuctionClosedException e) {
            System.out.println("Auction is not open");

        } catch (Exception e){
            System.out.println("System error");
        }
    }
}
