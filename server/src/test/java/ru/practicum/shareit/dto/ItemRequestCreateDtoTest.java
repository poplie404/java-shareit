package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemRequestCreateDtoSuccess() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need drill");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Need drill\"");
    }

    @Test
    void deserializeItemRequestCreateDtoSuccess() throws Exception {
        String json = "{\"description\":\"Need drill\"}";

        ItemRequestCreateDto dto = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need drill");
    }

    @Test
    void deserializeItemRequestCreateDtoNullDescription() throws Exception {
        String json = "{}";

        ItemRequestCreateDto dto = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isNull();
    }
}
