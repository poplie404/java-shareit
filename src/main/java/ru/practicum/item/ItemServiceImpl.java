package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Item createItem(ItemDto dto, Long ownerId) {
        Long id = idGenerator.incrementAndGet();
        dto.setId(id);
        dto.setUserId(ownerId);
        Item item = ItemMapper.toItem(dto);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, ItemDto dto, Long ownerId) {
        Item existing = items.get(itemId);
        if (existing == null) {
            throw new NoSuchElementException("Вещь с id " + itemId + " не найдена");
        }
        if (!Objects.equals(existing.getUserId(), ownerId)) {
            throw new IllegalStateException("Редактировать вещь может только владелец");
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existing.setAvailable(dto.getAvailable());

        items.put(itemId, existing);
        return existing;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NoSuchElementException("Вещь с id " + itemId + " не найдена");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getUserId(), ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String query = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(query))
                )
                .toList();
    }
}
