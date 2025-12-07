package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.item.ItemShortDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemRequestDtoSuccess() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 12, 0);

        ItemShortDto item1 = new ItemShortDto(10L, "Item 1");
        ItemShortDto item2 = new ItemShortDto(11L, "Item 2");

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(5L)
                .description("Need drill")
                .created(created)
                .items(List.of(item1, item2))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":5");
        assertThat(json).contains("\"description\":\"Need drill\"");
        assertThat(json).contains("\"created\":\"2024-01-01T12:00:00\"");
        assertThat(json).contains("\"items\":[");
        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"name\":\"Item 1\"");
        assertThat(json).contains("\"id\":11");
        assertThat(json).contains("\"name\":\"Item 2\"");
    }

    @Test
    void deserializeItemRequestDtoSuccess() throws Exception {
        String json = """
                {
                  "id": 5,
                  "description": "Need drill",
                  "created": "2024-01-01T12:00:00",
                  "items": [
                    {"id": 10, "name": "Item 1"},
                    {"id": 11, "name": "Item 2"}
                  ]
                }
                """;

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Need drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
        assertThat(dto.getItems()).hasSize(2);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(10L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Item 1");
        assertThat(dto.getItems().get(1).getId()).isEqualTo(11L);
        assertThat(dto.getItems().get(1).getName()).isEqualTo("Item 2");
    }
}
