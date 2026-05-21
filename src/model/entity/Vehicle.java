package model.entity;

import java.time.LocalDateTime;

public class Vehicle extends Item {

    private String brand;
    private int year;

    public Vehicle(String name,String description,double startPrice,
                   String brand,int year) {

        super(name,description,startPrice);
        this.brand = brand;
        this.year = year;
    }

    @Override
    public void printInfo() {
        System.out.println("ID: " + getId());
        System.out.println("Tên: " + getName());
        System.out.println("Trạng thái: "+getDescription());
        System.out.println("Giá khởi điểm: " + getStartPrice());
        System.out.println("Hãng: " + brand);
        System.out.println("Năm sản xuất: " + year);
    }
}