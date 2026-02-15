package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.entity.*;
import ru.practicum.shareit.repository.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void shouldCreateItemSuccessfully() {
        User owner = createUser("owner@test.com", "Owner");

        ItemDto dto = new ItemDto();
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);

        ItemDto created = itemService.createItem(dto, owner.getId());

        assertNotNull(created.getId());
        assertEquals("Test Item", created.getName());
        assertEquals("Test Description", created.getDescription());
        assertEquals(true, created.getAvailable());
    }

    @Test
    void shouldReturnItemById() {
        User owner = createUser("owner@test.com", "Owner");
        Item item = createItem(owner, "Test Item", true);

        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals("Test Item", result.getName());
    }

    @Test
    void shouldReturnItemsByOwner() {
        User owner = createUser("owner@test.com", "Owner");

        createItem(owner, "Item 1", true);
        createItem(owner, "Item 2", false);

        List<ItemDto> items = itemService.getItemsByOwner(owner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void shouldSearchItemsSuccessfully() {
        User owner = createUser("owner@test.com", "Owner");

        createItem(owner, "Drill Machine", true);
        createItem(owner, "Hammer Tool", true);

        List<ItemDto> results = itemService.search("drill");

        assertEquals(1, results.size());
        assertEquals("Drill Machine", results.get(0).getName());
    }


    @Test
    void shouldUpdateItemSuccessfully() {
        User owner = createUser("owner@test.com", "Owner");
        Item item = createItem(owner, "Old Name", true);

        ItemDto update = new ItemDto();
        update.setName("Updated Name");
        update.setDescription("Updated Description");

        ItemDto updated = itemService.updateItem(item.getId(), update, owner.getId());

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertTrue(updated.getAvailable()); // старое значение не было изменено
    }

    @Test
    void updateItemFailsForNonOwner() {
        User owner = createUser("owner@test.com", "Owner");
        User other = createUser("other@test.com", "Other");
        Item item = createItem(owner, "Item", true);

        ItemDto update = new ItemDto();
        update.setName("New Name");

        assertThrows(ru.practicum.shareit.exception.ForbiddenException.class,
                () -> itemService.updateItem(item.getId(), update, other.getId()));
    }

    @Test
    void createItemFailsWhenOwnerNotFound() {
        ItemDto dto = new ItemDto();
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        assertThrows(NoSuchElementException.class,
                () -> itemService.createItem(dto, 999L));
    }

    @Test
    void updateItemFailsWhenItemNotFound() {
        ItemDto dto = new ItemDto();
        dto.setName("New name");

        assertThrows(NoSuchElementException.class,
                () -> itemService.updateItem(999L, dto, 1L));
    }

    @Test
    void updateItemOnlyAvailability() {
        User owner = createUser("owner2@test.com", "Owner2");
        Item item = createItem(owner, "Name", false);

        ItemDto dto = new ItemDto();
        dto.setAvailable(true);

        ItemDto updated = itemService.updateItem(item.getId(), dto, owner.getId());

        assertTrue(updated.getAvailable());
        assertEquals("Name", updated.getName());
    }

    @Test
    void getItemByIdForNonOwnerDoesNotContainBookings() {
        User owner = createUser("owner3@test.com", "Owner3");
        User other = createUser("other3@test.com", "Other3");
        Item item = createItem(owner, "Item", true);

        ItemDto dto = itemService.getItemById(item.getId(), other.getId());

        assertEquals(item.getId(), dto.getId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void searchWithNullReturnsEmpty() {
        List<ItemDto> result = itemService.search(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchWithWhitespaceOnlyReturnsEmpty() {
        List<ItemDto> result = itemService.search("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchNullReturnsEmpty() {
        List<ItemDto> result = itemService.search(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchEmptyStringReturnsEmpty() {
        List<ItemDto> result = itemService.search("");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchWhitespaceReturnsEmpty() {
        List<ItemDto> result = itemService.search("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemByIdForNonOwner() {
        User owner = createUser("owner@test.com", "Owner");
        User other = createUser("other@test.com", "Other");
        Item item = createItem(owner, "Item", true);

        ItemDto dto = itemService.getItemById(item.getId(), other.getId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void createItemWithRequestId() {
        User owner = createUser("owner@test.com", "Owner");
        ItemRequest request = createItemRequest(createUser("requester@test.com", "Requester"));

        ItemDto dto = new ItemDto();
        dto.setName("Item from request");
        dto.setDescription("Created from request");
        dto.setAvailable(true);
        dto.setRequestId(request.getId());  // ← requestId!

        ItemDto created = itemService.createItem(dto, owner.getId());

        assertEquals("Item from request", created.getName());
        assertEquals(request.getId(), created.getRequestId());
    }

    @Test
    void createItemFailsWithInvalidRequestId() {
        User owner = createUser("owner@test.com", "Owner");

        ItemDto dto = new ItemDto();
        dto.setName("Item");
        dto.setRequestId(999L);  // несуществующий request

        assertThrows(NoSuchElementException.class,
                () -> itemService.createItem(dto, owner.getId()));
    }

    @Test
    void getItemsByOwnerWithBookingsAndComments() {
        User owner = createUser("owner@test.com", "Owner");
        Item item = createItem(owner, "Item with bookings", true);

        // Создать booking для last/next
        User booker = createUser("booker@test.com", "Booker");
        Booking booking = createBookingForItem(item, booker); // helper метод

        // Создать comment
        createComment(booker, item, "Great item!");

        List<ItemDto> items = itemService.getItemsByOwner(owner.getId());

        assertEquals(1, items.size());
        assertNotNull(items.get(0).getLastBooking());  // owner видит bookings
        assertFalse(items.get(0).getComments().isEmpty());
    }

    @Test
    void getItemByIdForOwnerWithBookings() {
        User owner = createUser("owner@test.com", "Owner");
        Item item = createItem(owner, "Owner sees bookings", true);

        User booker = createUser("booker@test.com", "Booker");

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));  // ПРОШЕЛ
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));    // ПРОШЕЛ
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));  // БУДУЩЕЕ
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));    // БУДУЩЕЕ
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        ItemDto dto = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(dto.getLastBooking());
        assertTrue(dto.getLastBooking() instanceof Map);
        assertNotNull(((Map<?, ?>) dto.getLastBooking()).get("id"));

        assertNotNull(dto.getNextBooking());
        assertTrue(dto.getNextBooking() instanceof Map);
        assertNotNull(((Map<?, ?>) dto.getNextBooking()).get("id"));
    }



    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Description for " + name);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private ItemRequest createItemRequest(User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }

    private Booking createBookingForItem(Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }

    private Comment createComment(User author, Item item, String text) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
