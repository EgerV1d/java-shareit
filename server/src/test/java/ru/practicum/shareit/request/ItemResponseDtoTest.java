package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemResponseDtoTest {

    @Test
    void shouldSetAndGetFields() {
        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getOwnerId()).isEqualTo(2L);
    }
}
