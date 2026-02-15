package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long ownerId, ItemCreateDto dto);

    ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
