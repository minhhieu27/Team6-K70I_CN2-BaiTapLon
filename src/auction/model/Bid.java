package auction.model;

import java.time.LocalDateTime;

public class Bid {
    private String bidder;
    private double amount;
    private LocalDateTime time;

    public Bid(String bidder, double amount){
        this.bidder = bidder;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public String getBidder(){
        return bidder;
    }

    public double getAmount(){
        return amount;
    }

    public LocalDateTime getDateTime(){
        return time;
    }
}
