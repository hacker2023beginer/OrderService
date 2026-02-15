package com.study.orderservice.controller;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.mapper.ItemMapper;
import com.study.orderservice.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody @Valid ItemDto itemDto){
        Item item = itemService.create(itemMapper.toEntity(itemDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemMapper.toDto(item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long id){
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItemById(@PathVariable Long id, @RequestBody ItemDto itemDto){
        Item item = itemService.updateItemById(id, itemDto);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long id){
        itemService.deleteItemById(id);
        return ResponseEntity.noContent().build();
    }
}
