package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    @Override
    public List<UserDto> getAllUsers() {
        var response = userClient.getAllUsers();

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    response.getBody() != null ? response.getBody().toString() : "Failed to get users"
            );
        }
        return response.getBody();
    }

    @Override
    public UserDto getUserById(Long id) {
        var response = userClient.getUser(id);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        if (response.getStatusCode().value() == 404) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to get user"
        );
    }

    @Override
    public UserDto createUser(UserDto dto) {
        var response = userClient.createUser(dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to create user"
        );
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        var response = userClient.updateUser(id, dto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new ResponseStatusException(
                response.getStatusCode(),
                response.getBody() != null ? response.getBody().toString() : "Failed to update user"
        );
    }

    @Override
    public void deleteUser(Long id) {
        var response = userClient.deleteUser(id);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(),
                    "Failed to delete user: " + (response.getBody() != null ? response.getBody().toString() : null)
            );
        }
    }
}
