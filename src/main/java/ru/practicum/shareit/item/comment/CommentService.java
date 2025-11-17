package ru.practicum.shareit.item.comment;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long itemId, CommentDto dto);

    List<CommentDto> getCommentsByItem(Long itemId);
}
