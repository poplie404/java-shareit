package ru.practicum.shareit.item.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return commentService.addComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}/comments")
    public List<CommentDto> getCommentsByItem(@PathVariable Long itemId) {
        return commentService.getCommentsByItem(itemId);
    }
}
