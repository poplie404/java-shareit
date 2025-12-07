package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentClient commentClient;
    private final ObjectMapper mapper;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        var response = commentClient.createComment(userId, itemId, dto);

        if (response.getStatusCode().is2xxSuccessful()) {
            return mapper.convertValue(response.getBody(), CommentDto.class);
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : null
        );
    }
}
