package auction.model;

import java.util.*;

import auction.strategy.BidStrategy;

import java.time.LocalDateTime;

public class Auction {
    
    private String itemName;
    private Money startPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Bid> bidHistory = new ArrayList<>();
    private BidStrategy strategy;

    public Auction(String itemName, Money startPrice, BidStrategy strategy){
        this.itemName = itemName;
        this.startPrice = startPrice;
        this.strategy = strategy;

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(5);
    }

    public BidStrategy getStrategy(){
        return strategy;
    }

    public void addBid(Bid bid){
        bidHistory.add(bid);
    }

    public Money getMiniumBid(){
        return getCurrentPrice().multiply(1.1);
    }

    public Money getCurrentPrice(){
        if (bidHistory.isEmpty()){
            return startPrice;
        }

        return bidHistory.get(bidHistory.size() - 1).getAmount();
    }

    public String getHighestBidder(){
        if (bidHistory.isEmpty()){
            return null;
        }

        return bidHistory.get(bidHistory.size() - 1).getBidder();
    }

    // ======= GETTER / SETTER =======
    public String getItemName(){
        return itemName;
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

    public List<Bid> getBidHistory(){
        return bidHistory;
    }
    
}
