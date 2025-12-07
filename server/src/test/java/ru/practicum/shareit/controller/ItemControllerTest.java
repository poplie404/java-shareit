package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.service.CommentService;
import ru.practicum.shareit.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;


    @Test
    void shouldCreateItemSuccessfully() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Drill");
        request.setDescription("Powerful drill");
        request.setAvailable(true);

        ItemDto response = new ItemDto();
        response.setId(1L);
        response.setName("Drill");
        response.setDescription("Powerful drill");
        response.setAvailable(true);

        Mockito.when(itemService.createItem(any(ItemDto.class), eq(5L)))
                .thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.available").value(true));
    }


    @Test
    void shouldReturnItemById() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Hammer");
        dto.setDescription("Heavy hammer");
        dto.setAvailable(true);

        Mockito.when(itemService.getItemById(eq(1L), eq(1L)))
                .thenReturn(dto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hammer"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturnOwnerItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item A");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item B");

        Mockito.when(itemService.getItemsByOwner(eq(10L)))
                .thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void shouldSearchItemsSuccessfully() throws Exception {
        ItemDto result = new ItemDto();
        result.setId(99L);
        result.setName("Super Drill");
        result.setDescription("Strong drill");
        result.setAvailable(true);

        Mockito.when(itemService.search(eq("drill")))
                .thenReturn(List.of(result));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(99))
                .andExpect(jsonPath("$[0].name").value("Super Drill"));
    }

    @Test
    void shouldUpdateItemSuccessfully() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Updated name");
        request.setDescription("Updated description");

        ItemDto response = new ItemDto();
        response.setId(1L);
        response.setName("Updated name");
        response.setDescription("Updated description");
        response.setAvailable(true);

        Mockito.when(itemService.updateItem(eq(1L), any(ItemDto.class), eq(5L)))
                .thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void shouldSearchItemsWhenTextIsBlank() throws Exception {
        Mockito.when(itemService.search(eq("")))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getItemNotFound() throws Exception {
        Mockito.when(itemService.getItemById(eq(999L), eq(1L)))
                .thenThrow(new ru.practicum.shareit.exception.NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())  // ✅ 404 вместо 400
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

}
