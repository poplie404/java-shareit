package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto dto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByUser(Long userId, String state);

    List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state);
}
