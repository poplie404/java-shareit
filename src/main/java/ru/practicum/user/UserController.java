package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        var users = userService.getAllUsers();
        log.info("Получен список пользователей: {} шт.", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получен пользователь с ID {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        UserDto added = userService.createUser(userDto);
        log.info("Пользователь добавлен: {}", added);
        return added;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID {}: {}", id, userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("Удалён пользователь с ID {}", id);
    }
}
