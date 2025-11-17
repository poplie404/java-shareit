package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Пользователь с id " + id + " не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalStateException(
                    "Пользователь с таким email уже существует: " + dto.getEmail()
            );
        }

        User user = UserMapper.toUser(dto);

        User saved = userRepository.save(user);

        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Пользователь с id " + id + " не найден"));

        if (dto.getEmail() != null) {
            boolean emailInUse =
                    userRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id);

            if (emailInUse) {
                throw new IllegalStateException(
                        "Пользователь с таким email уже существует: " + dto.getEmail()
                );
            }

            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        User saved = userRepository.save(existing);

        return UserMapper.toUserDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
    }
}
