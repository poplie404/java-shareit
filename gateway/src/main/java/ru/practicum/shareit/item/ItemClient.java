package ru.practicum.shareit.item;

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
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<ItemDto> createItem(long userId, ItemCreateDto body) {
        return postTyped("", userId, body, ItemDto.class);
    }

    public ResponseEntity<ItemDto> updateItem(long userId, long itemId, ItemUpdateDto body) {
        return patchTyped("/" + itemId, userId, body, ItemDto.class);
    }

    public ResponseEntity<ItemDto> getItem(long userId, long itemId) {
        return getTyped("/" + itemId, userId, ItemDto.class);
    }

    public ResponseEntity<List<ItemDto>> getOwnerItems(long userId) {
        return getListTyped("", userId, ItemDto.class);
    }

    public ResponseEntity<List<ItemDto>> search(String text) {
        return getListTyped("/search?text={text}", null, Map.of("text", text), ItemDto.class);
    }
}
