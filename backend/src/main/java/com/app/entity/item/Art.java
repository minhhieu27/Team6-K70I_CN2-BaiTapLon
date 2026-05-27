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
public class Art extends ItemEntity {

    private String artist;

    private String style;

    public Art(ItemType itemType,
        String itemName,
        String description,
        Money startPrice,
        String artist, 
        String style) {

        super(itemType, itemName, description, startPrice);

        this.artist = artist;
        this.style = style;
    }
}