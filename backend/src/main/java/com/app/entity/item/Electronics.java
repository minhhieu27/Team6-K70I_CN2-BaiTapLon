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
public class Electronics extends ItemEntity {

    private String brand;
    private String model;
    private String conditionStatus;
    private int warrantyMonths;
    private String color;
    private String storage;

    public Electronics(String itemName, String description, Money startPrice,
                        String brand,String model, String conditionStatus, String color, String storage, int warrantyMonths) {

        super(itemName, description, startPrice);
        
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.storage = storage;
        this.conditionStatus = conditionStatus;
        this.warrantyMonths = warrantyMonths;
    }
}