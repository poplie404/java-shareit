package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.item.ItemShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemShortDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemShortDtoSuccess() throws Exception {
        ItemShortDto dto = new ItemShortDto(1L, "Item name");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item name\"");
    }

    @Test
    void deserializeItemShortDtoSuccess() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item name\"}";

        ItemShortDto dto = objectMapper.readValue(json, ItemShortDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item name");
    }
}
