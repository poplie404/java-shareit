package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.comment.CommentClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @Override
    public ItemDto createItem(Long ownerId, ItemCreateDto dto) {
        var response = itemClient.createItem(ownerId, dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to create item"
        );
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId) {
        var response = itemClient.updateItem(ownerId, itemId, dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                "Failed to update item: " + (response.getBody() != null ? response.getBody().toString() : null)
        );
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        var response = itemClient.getItem(userId, itemId);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        if (response.getStatusCode().value() == 404) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                "Failed to get item: " + (response.getBody() != null ? response.getBody().toString() : null)
        );
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        var response = itemClient.getOwnerItems(ownerId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to get owner items: " + (response.getBody() != null ? response.getBody().toString() : null)
            );
        }
        return response.getBody();
    }

    @Override
    public List<ItemDto> search(String text) {
        var response = itemClient.search(text);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to search items: " + (response.getBody() != null ? response.getBody().toString() : null)
            );
        }
        return response.getBody();
    }
}
