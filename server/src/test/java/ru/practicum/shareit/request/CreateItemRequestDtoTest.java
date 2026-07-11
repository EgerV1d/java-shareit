package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateItemRequestDtoTest {

    @Test
    void shouldSetAndGetFields() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("Need a drill");

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }
}
