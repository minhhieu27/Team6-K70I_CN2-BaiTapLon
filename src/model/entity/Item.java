package model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Item extends Entity {

    private String name;
    private String description;
    private double startPrice;

    public Item(String name, String description, double startPrice) {

        super();
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;

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


    public abstract void printInfo();
}
