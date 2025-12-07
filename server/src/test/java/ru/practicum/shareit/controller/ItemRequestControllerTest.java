package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.service.ItemRequestService;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ItemRequestService itemRequestService;

    @Test
    void createItemRequestSuccess() throws Exception {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Need a drill");
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class))).thenReturn(responseDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getOwnItemRequestsSuccess() throws Exception {
        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto request2 = ItemRequestDto.builder()
                .id(2L)
                .description("Request 2")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getOwn(eq(1L))).thenReturn(List.of(request1, request2));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getAllItemRequestsSuccess() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("Other user request")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getAll(eq(1L), eq(0), eq(10))).thenReturn(List.of(request));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Other user request"));
    }

    @Test
    void getItemRequestByIdSuccess() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("Specific request")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getById(eq(1L), eq(1L))).thenReturn(request);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Specific request"));
    }
}