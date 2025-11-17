package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

public class BookingMapper {

    public static Booking toBooking(BookingRequestDto dto, Long bookerId) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(dto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    /**
     * Booking -> ResponseDto
     */
    public static BookingResponseDto toResponseDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(itemDto);
        dto.setBooker(userDto);
        return dto;
    }
}
