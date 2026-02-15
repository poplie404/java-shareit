package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.comment.CommentDto;
import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDto dto);

    List<CommentDto> getCommentsByItem(Long itemId);
}