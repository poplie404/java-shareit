package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;
    private final ObjectMapper mapper;

    @Override
    public List<UserDto> getAllUsers() {
        var response = userClient.getAllUsers();
        return mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));
    }

    @Override
    public UserDto getUserById(Long id) {
        var response = userClient.getUser(id);
        return mapper.convertValue(response.getBody(), UserDto.class);
    }

    @Override
    public UserDto createUser(UserDto dto) {
        try {
            var response = userClient.createUser(dto);
            return mapper.convertValue(response.getBody(), UserDto.class);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        var response = userClient.updateUser(id, dto);
        return mapper.convertValue(response.getBody(), UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        userClient.deleteUser(id);
    }
}
