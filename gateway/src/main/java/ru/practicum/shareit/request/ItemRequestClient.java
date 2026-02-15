package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<ItemRequestDto> createRequest(long userId, ItemRequestCreateDto body) {
        return postTyped("", userId, body, ItemRequestDto.class);
    }

    public ResponseEntity<List<ItemRequestDto>> getOwnRequests(long userId) {
        return getListTyped("", userId, ItemRequestDto.class);
    }

    public ResponseEntity<List<ItemRequestDto>> getAllRequests(long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return getListTyped("/all?from={from}&size={size}", userId, params, ItemRequestDto.class);
    }

    public ResponseEntity<ItemRequestDto> getRequest(long userId, long requestId) {
        return getTyped("/" + requestId, userId, ItemRequestDto.class);
    }
}
