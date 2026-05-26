package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.auction.CreateJewelryAuctionRequest;
import com.app.entity.item.ItemEntity;
import com.app.entity.item.Jewelry;

@Component
public class JewelryFactory implements ItemFactory<CreateJewelryAuctionRequest> {
    
    @Override
    public ItemType getType(){
        
        return ItemType.JEWELRY;
    }

    @Override
    public ItemEntity createItem(CreateJewelryAuctionRequest req){

        return new Jewelry(
                req.getItemName(), 
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getBrand(), 
                req.getModel(), 
                req.getMaterial(), 
                req.getWeight());
    }
}
