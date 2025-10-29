package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto dto) {
        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()));

        if (emailExists) {
            throw new IllegalStateException("Пользователь с таким email уже существует: " + dto.getEmail());
        }

        long id = idGenerator.incrementAndGet();
        User user = UserMapper.toUser(dto);
        user.setId(id);
        users.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = users.get(id);
        if (existing == null) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }

        if (dto.getEmail() != null) {
            boolean emailInUse = users.values().stream()
                    .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equalsIgnoreCase(dto.getEmail()));
            if (emailInUse) {
                throw new IllegalStateException("Пользователь с таким email уже существует: " + dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) existing.setName(dto.getName());

        return UserMapper.toUserDto(existing);
    }


    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
    }
}
