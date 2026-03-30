package auction.service;

import auction.model.*;

public class AutionService {
    private Auction auction;

    public AutionService(Auction auction){
        this.auction = auction;
    }

    public void placeBid(String bidder, double amount){
        Bid bid = new Bid(bidder, amount);

        boolean success = auction.placeBid(bid); // Xét coi bid có hợp lệ hay không

        if (success){ // Nếu hợp lệ
            System.out.println("Bid thanh cong!");
            System.out.println("Highest Bidder: " +auction.getHighestBidder());
            System.out.println("Current Price: " + auction.getCurrentPrice());
        }else{
            System.out.println("Bid that bai! (Gia thap hon)");
        }
    }
}
