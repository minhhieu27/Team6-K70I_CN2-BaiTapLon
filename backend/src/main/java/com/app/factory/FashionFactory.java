package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.item.CreateFashionAuctionRequest;
import com.app.entity.item.Fashion;
import com.app.entity.item.ItemEntity;

@Component
public class FashionFactory implements ItemFactory<CreateFashionAuctionRequest> {
    
    @Override
    public ItemType getType(){

        return ItemType.FASHION;
    }

    @Override
    public ItemEntity createItem(CreateFashionAuctionRequest req){

        return new Fashion(ItemType.FASHION,
                req.getItemName(), 
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getBrand(), 
                req.getSize(), 
                req.getModel(), 
                req.getColor(), 
                req.getMaterial());
    }
}
