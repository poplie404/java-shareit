package ru.practicum.shareit.item.comment;


public interface CommentService {

    CommentDto addComment(Long userId, Long itemId, CommentDto dto);

}
