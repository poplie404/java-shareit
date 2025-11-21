package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
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

        UserDto userDto = userService.getUserById(userId);
        User booker = userService.getUserEntity(userId);

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        validateBookingRequest(dto, item, userId);

        Booking booking = BookingMapper.toBooking(dto, item, booker);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponseDto(
                saved,
                ItemMapper.toItemDto(item),
                userDto
        );
    }



    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронь не найдена"));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Подтверждать бронирование может только владелец");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponseDto(
                saved,
                ItemMapper.toItemDto(item),
                userService.getUserById(saved.getBooker().getId())
        );
    }


    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронь не найдена"));

        Item item = booking.getItem();

        if (!booking.getBooker().getId().equals(userId)
                && !item.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("Нет доступа");
        }

        return BookingMapper.toResponseDto(
                booking,
                ItemMapper.toItemDto(item),
                userService.getUserById(booking.getBooker().getId())
        );
    }


    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state) {

        userService.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> list = switch (state.toUpperCase()) {
            case "ALL"      -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case "CURRENT"  -> bookingRepository
                    .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST"     -> bookingRepository
                    .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE"   -> bookingRepository
                    .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING"  -> bookingRepository
                    .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository
                    .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return list.stream()
                .map(b -> BookingMapper.toResponseDto(
                        b,
                        ItemMapper.toItemDto(itemRepository.findById(b.getItem().getId()).orElseThrow()),
                        userService.getUserById(b.getBooker().getId())
                ))
                .toList();
    }



    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state) {

        userService.getUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> list = switch (state.toUpperCase()) {
            case "ALL"      -> bookingRepository.findAllByOwnerId(ownerId);
            case "CURRENT"  -> bookingRepository.findCurrentByOwner(ownerId, now);
            case "PAST"     -> bookingRepository.findPastByOwner(ownerId, now);
            case "FUTURE"   -> bookingRepository.findFutureByOwner(ownerId, now);
            case "WAITING"  -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return list.stream()
                .map(b -> BookingMapper.toResponseDto(
                        b,
                        ItemMapper.toItemDto(itemRepository.findById(b.getItem().getId()).orElseThrow()),
                        userService.getUserById(b.getBooker().getId())
                ))
                .toList();
    }

    private void validateBookingRequest(BookingRequestDto dto, Item item, Long userId) {

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя бронировать свою вещь");
        }

        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и окончания обязательны");
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException("Дата начала должна быть раньше окончания");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала не может быть в прошлом");
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
