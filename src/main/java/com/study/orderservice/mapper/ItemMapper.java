package com.study.orderservice.mapper;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.dto.ItemRequestDto;
import com.study.orderservice.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toEntity(ItemDto itemDto);
    Item toEntity(ItemRequestDto itemDto);
}
