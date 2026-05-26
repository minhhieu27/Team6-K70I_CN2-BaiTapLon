package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.auction.CreateVehicleAuctionRequest;
import com.app.entity.item.ItemEntity;
import com.app.entity.item.Vehicle;

@Component
public class VehicleFactory implements ItemFactory<CreateVehicleAuctionRequest> {
    
    @Override
    public ItemType getType(){
        return ItemType.VEHICLE;
    }

    @Override
    public ItemEntity createItem(CreateVehicleAuctionRequest req){

        return new Vehicle(
                req.getItemName(), 
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getBrand(), 
                req.getModel(), 
                req.getMileage(), 
                req.getColor(), 
                req.getFuelType(), 
                req.getYear());
    }
}
