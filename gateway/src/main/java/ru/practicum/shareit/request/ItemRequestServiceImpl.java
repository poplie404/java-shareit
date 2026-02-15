package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestClient itemRequestClient;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        var response = itemRequestClient.createRequest(userId, dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to create request"
        );
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        var response = itemRequestClient.getOwnRequests(userId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    response.getBody() != null ? response.getBody().toString() : "Failed to get own requests"
            );
        }
        return response.getBody();
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        var response = itemRequestClient.getAllRequests(userId, from, size);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    response.getBody() != null ? response.getBody().toString() : "Failed to get all requests"
            );
        }
        return response.getBody();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        var response = itemRequestClient.getRequest(userId, requestId);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        if (response.getStatusCode().value() == 404) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to get request"
        );
    }
}
