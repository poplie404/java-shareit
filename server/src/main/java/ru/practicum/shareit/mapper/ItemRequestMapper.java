package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.item.ItemShortDto;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.ItemRequest;
import ru.practicum.shareit.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toResponseDto(ItemRequest request, List<Item> items) {
        if (request == null) {
            return null;
        }

        List<ItemShortDto> itemDtos = items == null ? List.of() :
                items.stream()
                        .map(this::toItemShortDto)
                        .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }

    private ItemShortDto toItemShortDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemShortDto dto = new ItemShortDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    public ItemRequest toEntity(ItemRequestCreateDto dto, User user) {
        if (dto == null || user == null) {
            return null;
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}