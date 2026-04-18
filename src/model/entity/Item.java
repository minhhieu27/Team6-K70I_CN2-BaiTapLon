package model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Item extends Entity {

    private String name;
    private String description;
    private double startPrice;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String currentWinner;
    private List<Double> bidHistory;
    public Item(String name, String description, double startPrice,
                LocalDateTime startTime, LocalDateTime endTime) {

        super();
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bidHistory = new ArrayList<>();
    }

    public enum AuctionStatus {    // tạo tập giá trị cố định
        NOT_STARTED,
        COMING_SOON,
        RUNNING,
        FINISHED
    }
    // trạng thái phiên đấu giá
    public AuctionStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(endTime))
            return AuctionStatus.FINISHED;

        if (now.isAfter(startTime) && now.isBefore(endTime))
            return AuctionStatus.RUNNING;

        Duration duration = Duration.between(now, startTime);
        long minutes = duration.toMinutes();

        if (minutes <= 10 && minutes >= 0) {
            return AuctionStatus.COMING_SOON;
        }

        return AuctionStatus.NOT_STARTED;
    }
    // logic đấu giá
    public boolean canBid() {
        return getStatus() == AuctionStatus.RUNNING;
    }

    public boolean isValidBid(double price) {
        return price > currentPrice;
    }

    public boolean placeBid(String user, double price) {

        if (!canBid())
            return false;

        if (!isValidBid(price))
            return false;

        currentPrice = price;
        currentWinner = user;
        bidHistory.add(price);

        return true;
    }

    public String getName() {
        return name;
    }
    public String getDescription(){
        return description;
    }
    public  double getStartPrice(){
        return startPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getCurrentWinner() {
        return currentWinner;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<Double> getBidHistory() {
        return bidHistory;
    }

    public abstract void printInfo();
}
