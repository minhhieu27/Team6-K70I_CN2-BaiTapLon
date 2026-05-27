package com.app.factory;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.money.Money;
import com.app.dto.request.item.CreateBookAuctionRequest;
import com.app.entity.item.Book;
import com.app.entity.item.ItemEntity;

@Component
public class BookFactory implements ItemFactory<CreateBookAuctionRequest> {
    
    @Override
    public ItemType getType(){

        return ItemType.BOOK;
    }

    @Override
    public ItemEntity createItem(CreateBookAuctionRequest req){

        return new Book(ItemType.BOOK,
                req.getItemName(),
                req.getDescription(), 
                new Money(req.getStartPrice()), 
                req.getAuthor(), 
                req.getPublisher(), 
                req.getPublishYear());
    }
}
