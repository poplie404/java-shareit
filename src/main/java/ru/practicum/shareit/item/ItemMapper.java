package ru.practicum.shareit.item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setName(item.getName());
        dto.setUserId(item.getUserId());
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
        item.setUserId(dto.getUserId());
        return item;
    }
}
