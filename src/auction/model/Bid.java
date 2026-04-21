package auction.model;

public class Bid {
    private String bidder;
    private Money amount;

    public Bid(String bidder, Money amount){
        this.bidder = bidder;
        this.amount = amount;
    }

    public String getBidder(){
        return bidder;
    }

    public Money getAmount(){
        return amount;
    }

    
}
