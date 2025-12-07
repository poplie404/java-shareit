package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestClient itemRequestClient;
    private final ObjectMapper mapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        var response = itemRequestClient.createRequest(userId, dto);
        return mapper.convertValue(response.getBody(), ItemRequestDto.class);
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        var response = itemRequestClient.getOwnRequests(userId);
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, ItemRequestDto.class));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        var response = itemRequestClient.getAllRequests(userId, from, size);
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, ItemRequestDto.class));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        var response = itemRequestClient.getRequest(userId, requestId);
        return mapper.convertValue(response.getBody(), ItemRequestDto.class);
    }
}
