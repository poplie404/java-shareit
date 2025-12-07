package ru.practicum.shareit.dto.item;

import lombok.Data;
import ru.practicum.shareit.dto.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Object lastBooking;

    private Object nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
