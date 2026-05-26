package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.auction.CreateArtAuctionRequest;
import com.app.entity.item.Art;
import com.app.entity.item.ItemEntity;

@Component
public class ArtFactory implements ItemFactory<CreateArtAuctionRequest> {
    
    @Override
    public ItemType getType(){

        return ItemType.ART;
    }

    @Override
    public ItemEntity createItem(CreateArtAuctionRequest req){

        return new Art(
                req.getItemName(),
                req.getDescription(),
                new Money(req.getStartPrice()),
                req.getArtist(),
                req.getStyle());
    }
}
