package ru.practicum.user;

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

        // частичное обновление
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());

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
