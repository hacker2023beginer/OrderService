package com.study.orderservice.service;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item getItemById(Long id);

    Item updateItemById(Long id, ItemDto dto);

    void deleteItemById(Long id);
}
