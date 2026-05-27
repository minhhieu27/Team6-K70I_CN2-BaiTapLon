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
public class Book extends ItemEntity {
    
    private String author;
    private String publisher;
    private int publishYear;

    public Book(ItemType itemType,
        String itemName,
        String description,
        Money startPrice,
                String author, String publisher, int publishYear){
                
        super(itemType, itemName, description, startPrice);

        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
    }
}
