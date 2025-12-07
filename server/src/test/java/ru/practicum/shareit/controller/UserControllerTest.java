package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.service.UserService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserService userService;

    @Test
    void createUserSuccess() throws Exception {
        UserDto requestDto = new UserDto();
        requestDto.setName("Test User");
        requestDto.setEmail("test@test.com");
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Test User");
        responseDto.setEmail("test@test.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(responseDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserByIdSuccess() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        when(userService.getUserById(eq(1L))).thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("User 1");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("User 2");
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    void updateUserSuccess() throws Exception {
        UserDto requestDto = new UserDto();
        requestDto.setName("Updated Name");
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Updated Name");
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(responseDto);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
    // ✅ getUserById NotFound (НЕ покрыто!)
    @Test
    void getUserByIdNotFound() throws Exception {
        when(userService.getUserById(eq(999L)))
                .thenThrow(new ru.practicum.shareit.exception.NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserDuplicateEmail() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Dup");
        dto.setEmail("dup@test.com");

        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new IllegalStateException("Email already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

}