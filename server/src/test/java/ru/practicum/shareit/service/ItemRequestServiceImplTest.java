package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.repository.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        // Очистка и создание пользователей перед каждым тестом
        userRepository.deleteAll();

        requester = new User();
        requester.setEmail("requester@test.com");
        requester.setName("Requester");
        requester = userRepository.save(requester);

        otherUser = new User();
        otherUser.setEmail("other@test.com");
        otherUser.setName("Other User");
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void createItemRequestSuccess() {
        // Arrange
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill for home renovation");

        // Act
        ItemRequestDto created = itemRequestService.create(requester.getId(), dto);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Need a drill for home renovation", created.getDescription());
        assertNotNull(created.getCreated());
        assertNotNull(created.getItems());
        assertTrue(created.getItems().isEmpty());
    }

    @Test
    void getOwnItemRequestsSuccess() {
        // Arrange - создаем несколько запросов
        ItemRequestCreateDto dto1 = new ItemRequestCreateDto();
        dto1.setDescription("First request");
        itemRequestService.create(requester.getId(), dto1);

        // Добавляем небольшую задержку для разного времени создания
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Second request");
        itemRequestService.create(requester.getId(), dto2);

        // Act
        List<ItemRequestDto> requests = itemRequestService.getOwn(requester.getId());

        // Assert
        assertEquals(2, requests.size());

        // Проверяем порядок - сначала самый новый (последний созданный)
        // Второй запрос должен быть первым в списке (created DESC)
        assertEquals("Second request", requests.get(0).getDescription());
        assertEquals("First request", requests.get(1).getDescription());
    }

    @Test
    void getAllItemRequestsSuccess() {
        // Arrange - создаем запрос от requester
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Test request");
        itemRequestService.create(requester.getId(), dto);

        // Act - otherUser получает запросы других пользователей
        List<ItemRequestDto> requests = itemRequestService.getAll(otherUser.getId(), 0, 10);

        // Assert
        assertEquals(1, requests.size());
        assertEquals("Test request", requests.get(0).getDescription());

        // Проверяем, что у запроса есть ID и время создания
        assertNotNull(requests.get(0).getId());
        assertNotNull(requests.get(0).getCreated());
        assertNotNull(requests.get(0).getItems());
    }

    @Test
    void getAllItemRequestsPagination() {
        // Arrange - создаем 3 запроса от requester
        for (int i = 1; i <= 3; i++) {
            ItemRequestCreateDto dto = new ItemRequestCreateDto();
            dto.setDescription("Request " + i);
            itemRequestService.create(requester.getId(), dto);
        }

        // Act - otherUser получает по 2 запроса за раз
        List<ItemRequestDto> firstPage = itemRequestService.getAll(otherUser.getId(), 0, 2);
        List<ItemRequestDto> secondPage = itemRequestService.getAll(otherUser.getId(), 2, 2);

        // Assert
        assertEquals(2, firstPage.size());
        assertEquals(1, secondPage.size());
    }

    @Test
    void getItemRequestByIdSuccess() {
        // Arrange
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Specific request");
        ItemRequestDto created = itemRequestService.create(requester.getId(), dto);

        // Act
        ItemRequestDto found = itemRequestService.getById(requester.getId(), created.getId());

        // Assert
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Specific request", found.getDescription());
        assertNotNull(found.getCreated());
        assertNotNull(found.getItems());
    }

    @Test
    void getOwnWhenNoRequestsReturnsEmptyList() {
        // Act
        List<ItemRequestDto> requests = itemRequestService.getOwn(requester.getId());

        // Assert
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void getAllWhenNoOtherRequestsReturnsEmptyList() {
        // Act - same user создал запрос, но получает запросы других пользователей
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("My own request");
        itemRequestService.create(requester.getId(), dto);

        List<ItemRequestDto> requests = itemRequestService.getAll(requester.getId(), 0, 10);

        // Assert - не должен видеть свой же запрос
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }
}