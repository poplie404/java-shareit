package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @PostMapping
    public Item createItem(@Valid @RequestBody ItemDto dto,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = itemService.createItem(dto, ownerId);
        log.info("Добавлена вещь {} владельцем ID={}", item.getName(), ownerId);
        return item;
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable Long itemId,
                           @RequestBody ItemDto dto,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item updated = itemService.updateItem(itemId, dto, ownerId);
        log.info("Обновлена вещь ID={} пользователем ID={}", itemId, ownerId);
        return updated;
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи ID={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<Item> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        List<Item> items = itemService.getItemsByOwner(ownerId);
        log.info("Получен список из {} вещей владельца ID={}", items.size(), ownerId);
        return items;
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по запросу: '{}'", text);
        return itemService.search(text);
    }
}
