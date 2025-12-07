package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRequestDtoTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validateBookingRequestDto_withNullFields_returnsViolations() {
        // Arrange
        BookingRequestDto dto = new BookingRequestDto();

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(3); // itemId, start, end
    }

    @Test
    void validateBookingRequestDto_withValidData_returnsNoViolations() {
        // Arrange
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validateBookingRequestDto_withStartInPast_returnsViolation() {
        // Arrange
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().minusDays(1)); // start в прошлом
        dto.setEnd(LocalDateTime.now().plusDays(1));

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty(); // должна быть ошибка, т.к. start в прошлом
    }

    @Test
    void validateBookingRequestDto_withEndInPast_returnsViolation() {
        // Arrange
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().minusDays(1)); // end в прошлом

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty(); // должна быть ошибка, т.к. end в прошлом
    }

    @Test
    void validateBookingRequestDto_withStartEqualToEnd_returnsNoViolationForFutureAnnotation() {
        // Arrange
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(sameTime);
        dto.setEnd(sameTime);

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}