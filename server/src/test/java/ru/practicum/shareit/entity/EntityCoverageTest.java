package ru.practicum.shareit.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EntityCoverageTest {

    @Test
    void userEntityFullCoverage() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@test.com", user.getEmail());

        assertTrue(user.toString().contains("id=1"));
        assertTrue(user.toString().contains("name=Test User"));
        assertTrue(user.toString().contains("email=test@test.com"));
    }

    @Test
    void itemEntityFullCoverage() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        ItemRequest request = new ItemRequest();
        request.setId(2L);

        Item item = new Item();
        item.setId(3L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        assertEquals(3L, item.getId());
        assertEquals("Drill", item.getName());
        assertEquals("Powerful drill", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void bookingEntityFullCoverage() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(2L);

        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        assertEquals(3L, booking.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), booking.getStart());
        assertEquals(LocalDateTime.of(2024, 1, 2, 10, 0), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void commentEntityFullCoverage() {
        User author = new User();
        author.setId(1L);

        Item item = new Item();
        item.setId(2L);

        Comment comment = new Comment();
        comment.setId(3L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2024, 1, 1, 12, 0));

        assertEquals(3L, comment.getId());
        assertEquals("Great item!", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), comment.getCreated());
    }

    @Test
    void itemRequestEntityFullCoverage() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(2L);
        request.setDescription("Need a drill");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.of(2024, 1, 1, 11, 0));

        assertEquals(2L, request.getId());
        assertEquals("Need a drill", request.getDescription());
        assertEquals(requester, request.getRequester());
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 0), request.getCreated());
    }

    @Test
    void entityNullFields() {
        // Проверяем работу с null полями (description в Item может быть null)
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Drill");
        item.setOwner(owner);

        assertNull(item.getDescription());
        assertNull(item.getRequest());
    }

    @Test
    void entityToStringCoverage() {
        User user = new User();
        user.setName("Test");

        assertNotNull(user.toString());

        Item item = new Item();
        item.setName("Test Item");

        assertNotNull(item.toString());
    }
}
