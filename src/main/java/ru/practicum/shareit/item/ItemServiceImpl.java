package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingServiceImpl bookingService;

    @Override
    public Item createItem(ItemDto dto, Long ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto dto, Long ownerId) {

        userService.getUserById(ownerId);

        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NoSuchElementException("Вещь с id " + itemId + " не найдена"));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existing.setAvailable(dto.getAvailable());

        return itemRepository.save(existing);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        Object lastBooking = null;
        Object nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {

            Booking last = bookingService.findLastBooking(itemId);
            Booking next = bookingService.findNextBooking(itemId);

            if (last != null) {
                lastBooking = Map.of(
                        "id", last.getId(),
                        "bookerId", last.getBooker().getId()
                );
            }

            if (next != null) {
                nextBooking = Map.of(
                        "id", next.getId(),
                        "bookerId", next.getBooker().getId()
                );
            }
        }

        return ItemMapper.toItemDto(
                item,
                lastBooking,
                nextBooking,
                comments
        );
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


