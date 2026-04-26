package com.study.orderservice.service;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.entity.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item getItemById(Long id);

    Item updateItemById(Long id, ItemDto dto);

    void deleteItemById(Long id);

    List<Item> getItems();
}
