package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingClient bookingClient;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, Long userId) {
        var response = bookingClient.bookItem(userId, bookingDto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        if (response.getStatusCode().value() == 404) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена");
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : null
        );
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        var response = bookingClient.approveBooking(ownerId, bookingId, approved);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to approve booking: " + response.getBody()
            );
        }
        return response.getBody();
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        var response = bookingClient.getBooking(userId, bookingId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Booking not found: " + response.getBody()
            );
        }
        return response.getBody();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state, Integer from, Integer size) {
        var response = bookingClient.getBookingsByBooker(userId, state, from, size);
        log.debug("STATUS: " + response.getStatusCode());
        log.debug("BODY size: " + (response.getBody() != null ? response.getBody().size() : 0));

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to get bookings: " + response.getBody()
            );
        }
        return response.getBody();
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, Integer from, Integer size) {
        var response = bookingClient.getBookingsByOwner(ownerId, state, from, size);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to get owner bookings: " + response.getBody()
            );
        }
        return response.getBody();
    }
}
