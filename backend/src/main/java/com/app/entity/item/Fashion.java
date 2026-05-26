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
public class Fashion extends ItemEntity{

    private String brand;
    private String size;
    private String model;
    private String color;
    private String material;

    public Fashion(String itemName, String description, Money startPrice,
                    String brand, String size, String model, String color, String material){
        super(itemName, description, startPrice);

        this.brand = brand;
        this.model = model;
        this.color = color;
        this.size = size;
        this.material = material;
    }
}
