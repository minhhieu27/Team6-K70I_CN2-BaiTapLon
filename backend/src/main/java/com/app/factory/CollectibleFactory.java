package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.item.CreateCollectibleAuctionRequest;
import com.app.entity.item.Collectible;
import com.app.entity.item.ItemEntity;

@Component
public class CollectibleFactory implements ItemFactory<CreateCollectibleAuctionRequest> {
    
    @Override
    public ItemType getType(){

        return ItemType.COLLECTIBLE;
    }

    @Override
    public ItemEntity createItem(CreateCollectibleAuctionRequest req){

        return new Collectible(ItemType.COLLECTIBLE,
                req.getItemName(), 
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getCategory(), 
                req.getRarity(), 
                req.getProductionYear());
    }
}
