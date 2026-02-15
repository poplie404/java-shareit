package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    void addCommentSuccess() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        CommentDto requestDto = new CommentDto();
        requestDto.setText("Test comment");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(10L);
        responseDto.setText("Test comment");
        responseDto.setAuthorName("Author Name");
        responseDto.setCreated(LocalDateTime.now());

        when(commentService.addComment(eq(userId), eq(itemId), any(CommentDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("Author Name"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getCommentsByItemSuccess() throws Exception {
        Long itemId = 2L;

        CommentDto c1 = new CommentDto();
        c1.setId(1L);
        c1.setText("First");
        c1.setAuthorName("User1");
        c1.setCreated(LocalDateTime.now());

        CommentDto c2 = new CommentDto();
        c2.setId(2L);
        c2.setText("Second");
        c2.setAuthorName("User2");
        c2.setCreated(LocalDateTime.now());

        when(commentService.getCommentsByItem(eq(itemId)))
                .thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/items/{itemId}/comments", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("First"))
                .andExpect(jsonPath("$[0].authorName").value("User1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Second"))
                .andExpect(jsonPath("$[1].authorName").value("User2"));
    }
}
