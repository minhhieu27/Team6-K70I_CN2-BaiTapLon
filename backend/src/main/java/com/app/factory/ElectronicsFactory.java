package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.auction.CreateElectronicsAuctionRequest;
import com.app.entity.item.Electronics;
import com.app.entity.item.ItemEntity;

@Component
public class ElectronicsFactory implements ItemFactory<CreateElectronicsAuctionRequest> {
    
    @Override
    public ItemType getType(){

        return ItemType.ELECTRONICS;
    }

    @Override
    public ItemEntity createItem(CreateElectronicsAuctionRequest req){

        return new Electronics(
                req.getItemName(), 
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getBrand(), 
                req.getModel(), 
                req.getConditionStatus(), 
                req.getColor(), 
                req.getStorage(), 
                req.getWarrantyMonths());
    }
}
