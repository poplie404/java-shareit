package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.entity.Comment;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public static Comment toComment(CommentDto dto, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());

        if (comment.getAuthor() != null) {
            dto.setAuthorName(comment.getAuthor().getName());
        }

        dto.setCreated(comment.getCreated());
        return dto;
    }
}