package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeUserDtoSuccess() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void deserializeUserDtoSuccess() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }
}
