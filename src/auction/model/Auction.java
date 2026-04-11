package auction.model;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import auction.observer.Observer;

import java.time.Duration;
import java.time.LocalDateTime;

public class Auction {
    
    private String itemName;
    private double startPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private List<Bid> bidHistory;
    private List<Observer> observers = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public Auction(String itemName, double startPrice){
        this.itemName = itemName;
        this.startPrice = startPrice;

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(5);

        this.status = AuctionStatus.SCHEDULED;

        this.bidHistory = new ArrayList<>();
    }

    public void addObserver(Observer o){ // Hàm đăng ký xem đấu giá
        observers.add(o);
    }

    public void removeObserver(Observer o){ // Hàm hủy đăng ký xem đấu giá
        observers.remove(o);
    }

    public void notifyObservers(String message){ // Thông báo 
        for (Observer o : observers){
            o.update(message);
        }
    }

    public void updateStatus(){ // Update trạng thái của phiên đấu giá
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

    public void paidItem(){ // Nếu đấu giá thành công thì hiện đã bán
        if (status == AuctionStatus.FINISHED){
            status = AuctionStatus.PAID;
        }
    }

    public void cancelledItem(){ // Hủy đấu giá
        if (status == AuctionStatus.SCHEDULED || status == AuctionStatus.OPEN){
            status = AuctionStatus.CANCELLED;
        }
    }

    public void extendAuctionTime(){ // Gia hạn thời gian

        long secondsLeft = Duration.between(LocalDateTime.now(), endTime).getSeconds();

        if (secondsLeft <= 30){
            endTime = endTime.plusSeconds(60);
        }
        System.out.println("Auction extended 60 seconds!");
    }
       
    public double getMiniumBid(){ // Giá đấu ít nhất phải bằng 1.1 giá hiện tại 
        return getCurrentPrice() * 1.1;
    }

    public boolean placeBid(Bid bid){ // Đấu giá

        lock.lock();
        try {
            updateStatus();
            
            if (status != AuctionStatus.OPEN){
                System.out.println("Auction is not open");
                return false;
    
            }
            
            if (bid.getAmount() <= getMiniumBid()){
                return false;
            }
    
            bidHistory.add(bid);
    
            extendAuctionTime();
            
            notifyObservers("New bid: " + bid.getAmount() + " by " + bid.getBidder());

            return true;
        } finally {
            lock.unlock();
        }

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
