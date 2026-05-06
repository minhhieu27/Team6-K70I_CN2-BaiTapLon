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
