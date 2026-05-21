package model.entity;

import java.time.LocalDateTime;

public class Electronics extends Item {

    private String brand;
    private String model;

    public Electronics(String name,String description,double startPrice,
                       String brand,String model) {

        super(name,description,startPrice);
        this.brand = brand;
        this.model = model;
    }

    @Override
    public void printInfo() {
        System.out.println("ID: " + getId());
        System.out.println("Tên: " + getName());
        System.out.println("Trạng thái: "+getDescription());
        System.out.println("Giá khởi điểm: " + getStartPrice());
        System.out.println("Hãng: " + brand);
        System.out.println("Model: " + model);
    }
}