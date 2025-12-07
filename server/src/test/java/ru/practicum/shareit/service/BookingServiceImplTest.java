package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.BookingRequestDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.entity.Booking;
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

        List<BookingResponseDto> bookings =
                bookingService.getBookingsByUser(booker.getId(), "ALL", 0, 10);

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
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

    @Test
    void getBookingsByUserWithUnknownStateThrows() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dto, booker.getId());

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByUser(booker.getId(), "UNKNOWN", 0, 10));
    }


    @Test
    void getBookingsByUserFutureState() {
        User owner = createUser("owner2@test.com", "Owner2");
        User booker = createUser("booker2@test.com", "Booker2");
        Item item = createItem(owner, "Test Item", true);

        // FUTURE: start > now
        BookingRequestDto dtoFuture = new BookingRequestDto();
        dtoFuture.setItemId(item.getId());
        dtoFuture.setStart(LocalDateTime.now().plusDays(1));
        dtoFuture.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dtoFuture, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getBookingsByUser(booker.getId(), "FUTURE", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByUserCurrentState() {
        User owner = createUser("owner3@test.com", "Owner3");
        User booker = createUser("booker3@test.com", "Booker3");
        Item item = createItem(owner, "Test Item", true);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusHours(1));  // start < now
        booking.setEnd(LocalDateTime.now().plusHours(1));     // end > now
        booking.setStatus(BookingStatus.APPROVED);            // approved для CURRENT
        bookingRepository.save(booking);

        List<BookingResponseDto> bookings = bookingService.getBookingsByUser(booker.getId(), "CURRENT", 0, 10);
        assertEquals(1, bookings.size());
    }


    @Test
    void getBookingsByUserWaitingState() {
        User owner = createUser("owner4@test.com", "Owner4");
        User booker = createUser("booker4@test.com", "Booker4");
        Item item = createItem(owner, "Test Item", true);

        // WAITING: статус WAITING
        BookingRequestDto dtoWaiting = new BookingRequestDto();
        dtoWaiting.setItemId(item.getId());
        dtoWaiting.setStart(LocalDateTime.now().plusDays(1));
        dtoWaiting.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.createBooking(dtoWaiting, booker.getId());

        // approveBooking меняет статус на APPROVED, поэтому оставляем WAITING
        // или создать напрямую через репозиторий с WAITING

        List<BookingResponseDto> bookings = bookingService.getBookingsByUser(booker.getId(), "WAITING", 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getBookingsByUserRejectedState() {
        User owner = createUser("owner5@test.com", "Owner5");
        User booker = createUser("booker5@test.com", "Booker5");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());

        // REJECTED: approve с false
        bookingService.approveBooking(booking.getId(), owner.getId(), false);

        List<BookingResponseDto> bookings = bookingService.getBookingsByUser(booker.getId(), "REJECTED", 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void getBookingsByUserUnknownState() {
        User booker = createUser("booker@test.com", "Booker");

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByUser(booker.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    void getBookingsByUserWithPagination() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Item", true);

        for (int i = 1; i <= 3; i++) {
            BookingRequestDto dto = new BookingRequestDto();
            dto.setItemId(item.getId());
            dto.setStart(LocalDateTime.now().plusDays(i));
            dto.setEnd(LocalDateTime.now().plusDays(i + 1));
            bookingService.createBooking(dto, booker.getId());
        }

        List<BookingResponseDto> firstPage = bookingService.getBookingsByUser(booker.getId(), "ALL", 1, 1);
        assertEquals(1, firstPage.size());
    }

    @Test
    void getBookingNotFound() {
        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> bookingService.getBooking(999L, 1L));
    }

    @Test
    void approveBookingNotFound() {
        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> bookingService.approveBooking(999L, 1L, true));
    }

    @Test
    void approveBookingFailsWhenNotWaiting() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test Item", true);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.createBooking(dto, booker.getId());

        bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approveBooking(booking.getId(), owner.getId(), false));
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

}
