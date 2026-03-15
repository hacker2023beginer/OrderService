package com.study.orderservice.service.impl;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.exception.ItemServiceException;
import com.study.orderservice.repository.ItemRepository;
import com.study.orderservice.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemServiceException("Item not found"));
    }

    @Override
    @Transactional
    public Item updateItemById(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemServiceException("Item not found"));
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }
}
