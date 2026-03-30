package auction.model;

public class Bid {
    private String bidder;
    private double amount;

    public Bid(String bidder, double amount){
        this.bidder = bidder;
        this.amount = amount;
    }

    public String getBidder(){
        return bidder;
    }

    public double getAmount(){
        return amount;
    }
}
