package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentClient commentClient;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        ResponseEntity<CommentDto> response = commentClient.createComment(userId, itemId, dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to create comment"
        );
    }
}
