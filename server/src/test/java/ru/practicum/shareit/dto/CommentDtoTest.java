package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.comment.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeCommentDtoSuccess() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Test comment");
        dto.setAuthorName("Author Name");
        dto.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Test comment\"");
        assertThat(json).contains("\"authorName\":\"Author Name\"");
        assertThat(json).contains("\"created\":\"2024-01-01T10:00:00\"");
    }

    @Test
    void deserializeCommentDtoSuccess() throws Exception {
        String json = """
                {
                  "id": 1,
                  "text": "Test comment",
                  "authorName": "Author Name",
                  "created": "2024-01-01T10:00:00"
                }
                """;

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Test comment");
        assertThat(dto.getAuthorName()).isEqualTo("Author Name");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }
}
