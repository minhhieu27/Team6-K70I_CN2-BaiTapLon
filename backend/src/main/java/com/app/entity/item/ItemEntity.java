package com.app.entity.item;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (length = 1000)
    private String description;

    @Column (nullable = false)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column (name = "start_price", nullable = false))
    private Money startPrice;

    public ItemEntity(ItemType itemType, String itemName, String description, Money startPrice){

        this.itemType = itemType;
        this.itemName = itemName;
        this.description = description;
        this.startPrice = startPrice;
    }

}
