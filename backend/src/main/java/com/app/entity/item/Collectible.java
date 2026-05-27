package com.app.entity.item;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Collectible extends ItemEntity {
    
    private String cagetory; // Loại đồ sưu tầm
    private String rarity; // Độ hiếm
    private int productionYear; //Thời gia phát hành

    public Collectible(ItemType itemType,
        String itemName,
        String description,
        Money startPrice,
                        String category, String rarity, int productionYear){
        
        super(itemType, itemName, description, startPrice);

        this.cagetory = category;
        this.rarity = rarity;
        this.productionYear = productionYear;
    }
}
