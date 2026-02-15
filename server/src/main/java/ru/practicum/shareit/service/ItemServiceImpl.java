package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.entity.Booking;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.ItemRequest;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.repository.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingServiceImpl bookingService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        ItemRequest request = null;

        if (dto.getRequestId() != null) {
            request = requestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("Запрос не найден"));
        }

        Item item = itemMapper.toItem(dto, request);
        item.setOwner(owner);

        item = itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto dto, Long ownerId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existing.setAvailable(dto.getAvailable());

        existing = itemRepository.save(existing);
        return itemMapper.toItemDto(existing);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList();

        Object lastBooking = null;
        Object nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            Booking last = bookingService.findLastBooking(itemId);
            Booking next = bookingService.findNextBooking(itemId);

            if (last != null)
                lastBooking = Map.of("id", last.getId(), "bookerId", last.getBooker().getId());

            if (next != null)
                nextBooking = Map.of("id", next.getId(), "bookerId", next.getBooker().getId());
        }

        return itemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(commentMapper::toCommentDto)
                            .toList();

                    Booking last = bookingService.findLastBooking(item.getId());
                    Booking next = bookingService.findNextBooking(item.getId());

                    Object lastBooking = last == null ? null :
                            Map.of("id", last.getId(), "bookerId", last.getBooker().getId());

                    Object nextBooking = next == null ? null :
                            Map.of("id", next.getId(), "bookerId", next.getBooker().getId());

                    return itemMapper.toItemDto(item, lastBooking, nextBooking, comments);
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank())
            return List.of();

        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
