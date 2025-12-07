package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.entity.Booking;
import ru.practicum.shareit.entity.BookingStatus;
import ru.practicum.shareit.entity.Comment;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addCommentSuccessWhenFinishedBookingExists() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test item", true);

        // бронирование, которое уже завершилось
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto request = new CommentDto();
        request.setText("Good item");

        CommentDto response = commentService.addComment(booker.getId(), item.getId(), request);

        assertNotNull(response.getId());
        assertEquals("Good item", response.getText());
        assertEquals(booker.getName(), response.getAuthorName());
        assertNotNull(response.getCreated());

        Comment saved = commentRepository.findById(response.getId()).orElseThrow();
        assertEquals(item.getId(), saved.getItem().getId());
        assertEquals(booker.getId(), saved.getAuthor().getId());
    }

    @Test
    void addCommentFailsWhenUserNotFound() {
        User owner = createUser("owner@test.com", "Owner");
        Item item = createItem(owner, "Test item", true);

        CommentDto request = new CommentDto();
        request.setText("Comment");

        assertThrows(NoSuchElementException.class,
                () -> commentService.addComment(999L, item.getId(), request));
    }

    @Test
    void addCommentFailsWhenItemNotFound() {
        User user = createUser("user@test.com", "User");

        CommentDto request = new CommentDto();
        request.setText("Comment");

        assertThrows(NoSuchElementException.class,
                () -> commentService.addComment(user.getId(), 999L, request));
    }

    @Test
    void addCommentFailsWhenNoFinishedBooking() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test item", true);

        // бронирование есть, но ещё НЕ завершилось
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto request = new CommentDto();
        request.setText("Comment");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.addComment(booker.getId(), item.getId(), request));
    }

    @Test
    void getCommentsByItemReturnsSavedComments() {
        User owner = createUser("owner@test.com", "Owner");
        User booker = createUser("booker@test.com", "Booker");
        Item item = createItem(owner, "Test item", true);

        // завершённое бронирование
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        // комментарий напрямую через репозиторий
        Comment comment = new Comment();
        comment.setText("Stored comment");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now().minusHours(1));
        commentRepository.save(comment);

        List<CommentDto> comments = commentService.getCommentsByItem(item.getId());

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals("Stored comment", comments.get(0).getText());
        assertEquals(booker.getName(), comments.get(0).getAuthorName());
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
