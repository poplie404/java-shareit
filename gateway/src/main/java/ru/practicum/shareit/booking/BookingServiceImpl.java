package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingClient bookingClient;
    private final ObjectMapper mapper;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, Long userId) {
        ResponseEntity<Object> response = bookingClient.bookItem(userId, bookingDto);

        if (response.getStatusCode().is2xxSuccessful()) {
            return mapper.convertValue(response.getBody(), BookingResponseDto.class);
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
            throw new RuntimeException("Server error: " + response.getStatusCode() + " " + response.getBody());
        }
        return mapper.convertValue(response.getBody(), BookingResponseDto.class);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        var response = bookingClient.getBooking(userId, bookingId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Server error: " + response.getStatusCode() + " " + response.getBody());
        }
        return mapper.convertValue(response.getBody(), BookingResponseDto.class);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state, Integer from, Integer size) {
        var response = bookingClient.getBookingsByBooker(userId, state, from, size);

        System.out.println("STATUS: " + response.getStatusCode());
        System.out.println("BODY: " + response.getBody());
        System.out.println("IS_2XX: " + response.getStatusCode().is2xxSuccessful());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Server error: " + response.getStatusCode() + " " + response.getBody());
        }

        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, BookingResponseDto.class));
    }


    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, Integer from, Integer size) {
        var response = bookingClient.getBookingsByOwner(ownerId, state, from, size);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Server error: " + response.getStatusCode() + " " + response.getBody());
        }
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, BookingResponseDto.class));
    }
}
