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
    public ItemDto createItem(@Valid @RequestBody ItemDto dto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = itemService.createItem(dto, ownerId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto dto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item updated = itemService.updateItem(itemId, dto, ownerId);
        return ItemMapper.toItemDto(updated);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getItemById(itemId, userId);
    }



    @GetMapping
    public Collection<ItemDto> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        List<Item> items = itemService.getItemsByOwner(ownerId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }



    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text
    ) {
        List<Item> items = itemService.search(text);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

}
