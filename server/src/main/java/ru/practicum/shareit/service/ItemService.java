package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.item.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto dto, Long ownerId);

    ItemDto updateItem(Long itemId, ItemDto dto, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
