package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void toDto_shouldMapCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        ItemDto dto = mapper.toDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void toDto_shouldReturnNullWhenItemIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_shouldMapCorrectly() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);

        User owner = new User();
        owner.setId(1L);

        Item item = mapper.toEntity(dto, owner);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null, new User())).isNull();
    }
}
