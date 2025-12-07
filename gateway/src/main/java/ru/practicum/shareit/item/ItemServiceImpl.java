package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.CommentClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemClient itemClient;
    private final CommentClient commentClient;
    private final ObjectMapper mapper;

    @Override
    public ItemDto createItem(Long ownerId, ItemCreateDto dto) {
        var response = itemClient.createItem(ownerId, dto);
        return mapper.convertValue(response.getBody(), ItemDto.class);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId) {
        var response = itemClient.updateItem(ownerId, itemId, dto);
        return mapper.convertValue(response.getBody(), ItemDto.class);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        var response = itemClient.getItem(userId, itemId);
        return mapper.convertValue(response.getBody(), ItemDto.class);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        var response = itemClient.getOwnerItems(ownerId);
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));
    }

    @Override
    public List<ItemDto> search(String text) {
        var response = itemClient.search(text);
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));
    }

}
