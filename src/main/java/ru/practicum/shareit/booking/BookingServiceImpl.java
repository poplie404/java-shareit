package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto dto, Long userId) {

        UserDto user = userService.getUserById(userId);

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя бронировать свою вещь");
        }

        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и окончания обязательны");
        }
        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала не может быть в прошлом");
        }

        Booking booking = BookingMapper.toBooking(dto, userId);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponseDto(
                saved,
                ItemMapper.toItemDto(item),
                user
        );
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow();

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Подтверждать бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponseDto(
                saved,
                ItemMapper.toItemDto(item),
                userService.getUserById(saved.getBookerId())
        );
    }


    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow();

        if (!booking.getBookerId().equals(userId)
                && !item.getOwnerId().equals(userId)) {
            throw new IllegalStateException("Нет доступа к этому бронированию");
        }

        return BookingMapper.toResponseDto(
                booking,
                ItemMapper.toItemDto(item),
                userService.getUserById(booking.getBookerId())
        );
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state) {

        userService.getUserById(userId);

        List<Booking> list = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        return filterByState(list, state).stream()
                .map(b -> BookingMapper.toResponseDto(
                        b,
                        ItemMapper.toItemDto(itemRepository.findById(b.getItemId()).orElseThrow()),
                        userService.getUserById(b.getBookerId())
                ))
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state) {

        userService.getUserById(ownerId);

        List<Booking> list = bookingRepository.findAllByOwnerId(ownerId);

        return filterByState(list, state).stream()
                .map(b -> BookingMapper.toResponseDto(
                        b,
                        ItemMapper.toItemDto(itemRepository.findById(b.getItemId()).orElseThrow()),
                        userService.getUserById(b.getBookerId())
                ))
                .toList();
    }

    private List<Booking> filterByState(List<Booking> list, String state) {

        LocalDateTime now = LocalDateTime.now();

        return switch (state.toUpperCase()) {
            case "ALL"      -> list;
            case "CURRENT"  -> list.stream()
                    .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                    .toList();
            case "PAST"     -> list.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .toList();
            case "FUTURE"   -> list.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .toList();
            case "WAITING"  -> list.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .toList();
            case "REJECTED" -> list.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .toList();
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };
    }
}
