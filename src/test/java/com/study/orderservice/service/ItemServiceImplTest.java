package com.study.orderservice.service;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.exception.ItemServiceException;
import com.study.orderservice.repository.ItemRepository;
import com.study.orderservice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_shouldSaveItem() {
        Item item = new Item();
        item.setName("Test");
        item.setPrice(10.0);

        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.create(item);

        assertThat(result).isSameAs(item);
        verify(itemRepository).save(item);
    }

    @Test
    void getItemById_shouldReturnItem_whenExists() {
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItemById(1L);

        assertThat(result).isSameAs(item);
        verify(itemRepository).findById(1L);
    }

    @Test
    void getItemById_shouldThrow_whenNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(1L))
                .isInstanceOf(ItemServiceException.class)
                .hasMessage("Item not found");
    }

    @Test
    void updateItemById_shouldUpdateAndSave_whenExists() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setName("Old");
        existing.setPrice(5.0);

        ItemDto dto = new ItemDto();
        dto.setName("New");
        dto.setPrice(15.0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(existing)).thenReturn(existing);

        Item result = itemService.updateItemById(1L, dto);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getPrice()).isEqualTo(15.0);
        verify(itemRepository).save(existing);
    }

    @Test
    void updateItemById_shouldThrow_whenNotFound() {
        ItemDto dto = new ItemDto();
        dto.setName("New");
        dto.setPrice(10.0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItemById(1L, dto))
                .isInstanceOf(ItemServiceException.class)
                .hasMessage("Item not found");
    }

    @Test
    void deleteItemById_shouldCallRepository() {
        itemService.deleteItemById(1L);

        verify(itemRepository).deleteById(1L);
    }
}

