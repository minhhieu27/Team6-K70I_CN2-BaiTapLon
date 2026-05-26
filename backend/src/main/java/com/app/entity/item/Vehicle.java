package com.app.entity.item;

import com.app.common.money.Money;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vehicle extends ItemEntity {

    private String brand;
    private String model;
    private String fuelType;
    private String color;
    private Long mileage;
    private int year;

    public Vehicle(String itemName, String description, Money startPrice,
                    String brand, String model, Long mileage, String color, String fuelType, int year) {

        super(itemName, description, startPrice);

        this.brand = brand;
        this.model = model;
        this.color = color;
        this.mileage = mileage;
        this.fuelType = fuelType;
        this.year = year;
    }
}