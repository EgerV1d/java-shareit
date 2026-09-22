package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ItemForBookingDto;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemForBookingDtoTest {

    @Test
    void shouldSetAndGetFields() {
        ItemForBookingDto dto = new ItemForBookingDto();
        dto.setId(1L);
        dto.setName("Test Item");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
    }
}
