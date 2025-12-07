package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemDto dto, Long ownerId);

    Item updateItem(Long itemId, ItemDto dto, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> search(String text);
}
