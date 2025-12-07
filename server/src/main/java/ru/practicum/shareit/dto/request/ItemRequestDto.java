package ru.practicum.shareit.dto.request;

import lombok.*;
import ru.practicum.shareit.dto.item.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemShortDto> items;
}
