package ru.practicum.shareit.item;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(new ArrayList<>());

        return dto;
    }

    public static Item toItem(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(dto.getId());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setName(dto.getName());
        return item;
    }
}
