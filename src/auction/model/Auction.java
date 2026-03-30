package auction.model;

import java.util.*;
public class Auction {
    
    private String itemName;
    private double currentPrice;
    private String highestBidder;
    private double startPrice;

    private List<Bid> bidHistory;

    public Auction(String itemName, double startPrice){
        this.itemName = itemName;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.bidHistory = new ArrayList<>();
    }
       
    public double getMiniumBid(){ // Giá tăng thêm phải bằng giá hiện tại + 1/10 giá gốc
        return currentPrice + startPrice * 0.1;
    }

    public boolean placeBid(Bid bid){
        if (bid.getAmount() < getMiniumBid() ){
            return false;
        }

        currentPrice = bid.getAmount();
        highestBidder = bid.getBidder();
        bidHistory.add(bid);

        return true;
    }

    public String getItemName(){
        return itemName;
    }

    public double getCurrentPrice(){
        return currentPrice;
    }

    public String getHighestBidder(){
        return highestBidder;
    }

    public List<Bid> getBidHistory(){
        return bidHistory;
    }

    public void printBidHistory(){
        for (Bid b : bidHistory){
            System.out.println(b.getBidder() + " bid " + b.getAmount());
        }
    }
}
