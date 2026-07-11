package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toEntity_shouldMapCorrectly() {
        String description = "Need a drill";
        User requester = new User();
        requester.setId(1L);
        requester.setName("Requester");

        ItemRequest request = mapper.toEntity(description, requester);

        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void toDto_shouldMapCorrectly() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");

        User requester = new User();
        requester.setId(1L);
        requester.setName("Requester");
        request.setRequester(requester);

        LocalDateTime created = LocalDateTime.now();
        request.setCreated(created);

        ItemRequestDto dto = mapper.toDto(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getRequesterId()).isEqualTo(1L);
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void toDto_shouldReturnNullWhenRequestIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDtoWithItems_shouldMapCorrectly() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");

        User requester = new User();
        requester.setId(1L);
        request.setRequester(requester);

        LocalDateTime created = LocalDateTime.now();
        request.setCreated(created);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        List<Item> items = List.of(item);

        ItemRequestDto dto = mapper.toDtoWithItems(request, items);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(2L);
    }

    @Test
    void toDtoWithItems_shouldReturnNullWhenRequestIsNull() {
        assertThat(mapper.toDtoWithItems(null, List.of())).isNull();
    }

    @Test
    void toDtoWithItems_shouldHandleEmptyItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");

        User requester = new User();
        requester.setId(1L);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        ItemRequestDto dto = mapper.toDtoWithItems(request, List.of());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toDtoWithItems_shouldHandleNullItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");

        User requester = new User();
        requester.setId(1L);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        ItemRequestDto dto = mapper.toDtoWithItems(request, null);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }
}
