package ru.practicum.item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemDto dto, Long ownerId);

    Item updateItem(Long itemId, ItemDto dto, Long ownerId);

    Item getItemById(Long itemId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> search(String text);
}
