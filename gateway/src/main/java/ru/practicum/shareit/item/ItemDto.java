package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private Long requestId;

    private Object lastBooking;
    private Object nextBooking;
    private List<CommentDto> comments;
}
