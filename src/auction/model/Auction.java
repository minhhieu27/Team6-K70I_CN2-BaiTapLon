package auction.model;

import java.util.*;
import java.time.LocalDateTime;

public class Auction {
    
    private String itemName;
    private double startPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;

    private List<Bid> bidHistory;

    public Auction(String itemName, double startPrice){
        this.itemName = itemName;
        this.startPrice = startPrice;

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(5);

        this.status = AuctionStatus.SCHEDULED;

        this.bidHistory = new ArrayList<>();
    }

    public void updateStatus(){
        LocalDateTime now = LocalDateTime.now();

        if (status == AuctionStatus.CANCELLED || status == AuctionStatus.PAID){
            return;
        }

        if (now.isBefore(startTime)){
            status = AuctionStatus.SCHEDULED;
        }

        else if (now.isAfter(startTime) && now.isBefore(endTime)){
            status = AuctionStatus.OPEN;
        }

        else if (now.isAfter(endTime)){
            status = AuctionStatus.FINISHED;
        }
    }

    public AuctionStatus getStatus(){
        updateStatus();
        return status;
    }

    public void paidItem(){
        if (status == AuctionStatus.FINISHED){
            status = AuctionStatus.PAID;
        }
    }

    public void cancelledItem(){
        if (status == AuctionStatus.SCHEDULED || status == AuctionStatus.OPEN){
            status = AuctionStatus.CANCELLED;
        }
    }
       
    public double getMiniumBid(){ // Giá đấu ít nhất phải bằng 1.1 giá hiện tại 
        return getCurrentPrice() * 1.1;
    }

    public boolean placeBid(Bid bid){

        updateStatus();

        if (status != AuctionStatus.OPEN){
            System.out.println("Auction is not open");
            return false;

        }
        if (bid.getAmount() <= getMiniumBid()){
            return false;
        }

        bidHistory.add(bid);

        System.out.println("New highest bidder: " + bid.getBidder() + "| Price: " + bid.getAmount());

        return true;
    }

    public String getItemName(){
        return itemName;
    }

    public double getCurrentPrice(){
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

    public List<Bid> getBidHistory(){
        return bidHistory;
    }

    public void printBidHistory(){
        for (Bid b : bidHistory){
            System.out.println(b.getBidder() + " bid " + b.getAmount());
        }
    }

    @Override
    public String toString(){
        return "Item: " + itemName +
                "\nStatus: " + status +
                "\nHighest Bidder: " + getHighestBidder() +
                "\nCurrent Price: " + getCurrentPrice();
    }
}
