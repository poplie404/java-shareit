package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;

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
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        List<ItemDto> results = itemService.search("");
        assertTrue(results.isEmpty());
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
}
