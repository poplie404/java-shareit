package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.entity.BookingStatus;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {
    @Autowired private ObjectMapper objectMapper;

    @Test
    void serializeBookingResponseDtoSuccess() throws Exception {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        dto.setStatus(BookingStatus.WAITING);
        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("\"start\":\"2024-01-01T10:00:00\"");
    }

    @Test
    void deserializeBookingResponseDtoSuccess() throws Exception {
        String json = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\",\"status\":\"APPROVED\"}";
        BookingResponseDto dto = objectMapper.readValue(json, BookingResponseDto.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }
}