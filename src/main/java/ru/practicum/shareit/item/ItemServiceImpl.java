package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(ItemDto dto, Long ownerId) {

        userService.getUserById(ownerId);

        Item item = ItemMapper.toItem(dto);
        item.setOwnerId(ownerId);

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto dto, Long ownerId) {

        userService.getUserById(ownerId);

        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NoSuchElementException("Вещь с id " + itemId + " не найдена"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            existing.setAvailable(dto.getAvailable());
        }

        return itemRepository.save(existing);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(c -> CommentMapper.toCommentDto(
                        c,
                        userRepository.findById(c.getAuthorId()).orElseThrow().getName()
                ))
                .toList();

        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(comments);

        dto.setLastBooking(null);
        dto.setNextBooking(null);

        return dto;
    }



    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toLowerCase());
    }
}
