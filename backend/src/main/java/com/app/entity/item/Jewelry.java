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
public class Jewelry extends ItemEntity {
    
    private String material;
    private double weight;
    private String brand;
    private String model;

    public Jewelry(String itemName, String description, Money startPrice,
                    String brand, String model, String material, double weight){
        
        super(itemName, description, startPrice);

        this.brand = brand;
        this.model = model;
        this.material = material;
        this.weight = weight;
    }
}
