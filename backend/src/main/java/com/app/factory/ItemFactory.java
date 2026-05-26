package com.app.factory;

import com.app.common.enums.ItemType;
import com.app.entity.item.ItemEntity;

public interface ItemFactory<T> {
    
    ItemType getType();

    ItemEntity createItem(T req);
}
