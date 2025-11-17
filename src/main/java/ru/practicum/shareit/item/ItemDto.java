package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Доступность обязательна")
    private Boolean available;

    private Object lastBooking;
    private Object nextBooking;
    private List<CommentDto> comments;
}
