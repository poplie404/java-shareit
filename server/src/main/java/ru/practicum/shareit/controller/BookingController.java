package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.BookingRequestDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.service.BookingService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingRequestDto dto) {
        return service.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam boolean approved
    ) {
        return service.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.getBookingsByOwner(ownerId, state, from, size);
    }
}
