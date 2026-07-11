package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentRequestDtoTest {

    @Test
    void shouldSetAndGetFields() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Great item!");

        assertThat(dto.getText()).isEqualTo("Great item!");
    }
}
