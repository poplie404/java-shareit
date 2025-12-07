package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.booking.BookingRequestDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.entity.Booking;
import ru.practicum.shareit.entity.BookingStatus;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponseDto toResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        // Полный ItemDto
        Item item = booking.getItem();
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(List.of());
        dto.setItem(itemDto);

        // Полный UserDto
        User user = booking.getBooker();
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        dto.setBooker(userDto);

        return dto;
    }

    public Booking toBooking(BookingRequestDto dto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

}


