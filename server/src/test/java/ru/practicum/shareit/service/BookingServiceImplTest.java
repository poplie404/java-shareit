package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.BookingRequestDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.entity.BookingStatus;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void shouldCreateBookingSuccessfully() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());

        assertNotNull(booking);
        assertEquals(item.getId(), booking.getItem().getId());
        assertEquals(booker.getId(), booking.getBooker().getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void shouldApproveBookingSuccessfully() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());
        BookingResponseDto approved = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void shouldReturnBookingsByUser() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(dto, booker.getId());

        // 👍 исправлено: добавлены from и size
        List<BookingResponseDto> bookings =
                bookingService.getBookingsByUser(booker.getId(), "ALL", 0, 10);

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    private User createUser(String email, String name) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        return userRepository.save(u);
    }

    private Item createItem(User owner, String name, boolean available) {
        Item i = new Item();
        i.setName(name);
        i.setDescription("Description");
        i.setAvailable(available);
        i.setOwner(owner);
        return itemRepository.save(i);
    }

    @Test
    void createBookingFailsWhenItemNotAvailable() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", false); // available = false

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBookingFailsWhenStartInPast() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().minusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void approveBookingFailsWhenNotOwner() {
        User owner = createUser("owner@test.com", "Owner");
        User other = createUser("other@test.com", "Other");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());

        assertThrows(ru.practicum.shareit.exception.ForbiddenException.class,
                () -> bookingService.approveBooking(booking.getId(), other.getId(), true));
    }

    @Test
    void getBookingFailsWhenNoAccess() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        User stranger = createUser("stranger@test.com", "Stranger");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());

        assertThrows(ru.practicum.shareit.exception.ForbiddenException.class,
                () -> bookingService.getBooking(booking.getId(), stranger.getId()));
    }

}
