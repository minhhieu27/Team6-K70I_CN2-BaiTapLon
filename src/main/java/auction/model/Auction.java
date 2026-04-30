package auction.model;

import java.util.*;

import auction.strategy.BidStrategy;

import java.time.LocalDateTime;

public class Auction {
    
    private final String itemName;
    private final Money startPrice;
    private final  BidStrategy strategy;

    private final LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime = startTime.plusMinutes(5);
    
    private final List<Bid> bidHistory = new ArrayList<>();
    private AuctionStatus status = AuctionStatus.SCHEDULED;
    
    public Auction(String itemName, Money startPrice, BidStrategy strategy){
        this.itemName = itemName;
        this.startPrice = startPrice;
        this.strategy = strategy;
        
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
    public BidStrategy getStrategy(){
        return strategy;
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
        
        return bidHistory.get(bidHistory.size() - 1).getUserId();
    }

    public AuctionStatus geStatus(){
        return status;
    }

    public void setStatus(AuctionStatus status){
        this.status = status;
    }
    
    public void addBid(Bid bid){
        if (bid == null) throw new IllegalArgumentException();
        bidHistory.add(bid);
    }
}
