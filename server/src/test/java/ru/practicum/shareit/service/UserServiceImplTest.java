package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.repository.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @Test
    void createUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@test.com");
        UserDto created = userService.createUser(userDto);
        assertNotNull(created);
        assertEquals("Test User", created.getName());
        assertEquals("test@test.com", created.getEmail());
    }

    @Test
    void getUserByIdSuccess() {
        User user = createUser("test@test.com", "Test User");
        UserDto result = userService.getUserById(user.getId());
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    void getAllUsersSuccess() {
        createUser("user1@test.com", "User 1");
        createUser("user2@test.com", "User 2");
        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void updateUserSuccess() {
        User user = createUser("old@test.com", "Old Name");
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@test.com");
        UserDto updated = userService.updateUser(user.getId(), updateDto);
        assertEquals("New Name", updated.getName());
        assertEquals("new@test.com", updated.getEmail());
    }

    @Test
    void deleteUserSuccess() {
        User user = createUser("delete@test.com", "To Delete");
        userService.deleteUser(user.getId());
        assertFalse(userRepository.existsById(user.getId()));
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }
}