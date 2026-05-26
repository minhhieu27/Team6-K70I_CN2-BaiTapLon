package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.item.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
