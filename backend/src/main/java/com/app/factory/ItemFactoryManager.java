package com.app.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.entity.item.ItemEntity;
import com.app.exception.item.InvalidItemTypeException;

@Component
public class ItemFactoryManager {
    
    private final List<ItemFactory<?>> fatories;

    public ItemFactoryManager(List<ItemFactory<?>> factories){
        this.fatories = factories;
    }

    @SuppressWarnings("unchecked")
    public ItemEntity createItem(ItemType type, Object req){

        ItemFactory<Object> factory = (ItemFactory<Object>) fatories.stream().filter(f -> f.getType() == type).findFirst().orElseThrow(() -> new InvalidItemTypeException("Loại hàng không hợp lệ"));

        return factory.createItem(req);
    }
}
