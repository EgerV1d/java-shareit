package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookerDto;

import static org.assertj.core.api.Assertions.assertThat;

public class BookerDtoTest {

    @Test
    void shouldSetAndGetFields() {
        BookerDto dto = new BookerDto();
        dto.setId(1L);
        dto.setName("Booker");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Booker");
    }
}
