package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.booking.BookingRequestDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.entity.Booking;
import ru.practicum.shareit.entity.BookingStatus;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto dto, Long userId) {

        User booker = userService.getUserEntity(userId);

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        validateBooking(dto, item, userId);

        Booking booking = bookingMapper.toBooking(dto, item, booker);
        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toResponseDto(saved);
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Подтверждать бронирование может только владелец");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toResponseDto(saved);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Нет доступа к бронированию");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state, int from, int size) {

        userService.getUserEntity(userId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return list.stream()
                .skip(from)
                .limit(size)
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, int from, int size) {

        userService.getUserEntity(ownerId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByOwnerId(ownerId);
            case "CURRENT" -> bookingRepository.findCurrentByOwner(ownerId, now);
            case "PAST" -> bookingRepository.findPastByOwner(ownerId, now);
            case "FUTURE" -> bookingRepository.findFutureByOwner(ownerId, now);
            case "WAITING" -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return list.stream()
                .skip(from)
                .limit(size)
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    private void validateBooking(BookingRequestDto dto, Item item, Long userId) {
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя бронировать свою вещь");
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Даты обязательны");
        }
        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException("Некорректный интервал");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Начало не может быть в прошлом");
        }
    }

    public Booking findLastBooking(Long itemId) {
        List<Booking> list = bookingRepository.findLastBookingRaw(itemId, Pageable.ofSize(1));
        return list.isEmpty() ? null : list.get(0);
    }

    public Booking findNextBooking(Long itemId) {
        List<Booking> list = bookingRepository.findNextBookingRaw(itemId, Pageable.ofSize(1));
        return list.isEmpty() ? null : list.get(0);
    }
}
