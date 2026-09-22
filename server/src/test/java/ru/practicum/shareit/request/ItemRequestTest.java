package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRequestTest {
    @Test
    void shouldCreateItemRequest() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("Requester");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void shouldSetAllFields() {
        ItemRequest request = new ItemRequest();
        User requester = new User();
        requester.setId(2L);
        requester.setName("Another Requester");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        request.setId(2L);
        request.setDescription("Need a hammer");
        request.setRequester(requester);
        request.setCreated(created);

        assertThat(request.getId()).isEqualTo(2L);
        assertThat(request.getDescription()).isEqualTo("Need a hammer");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(created);
    }

    @Test
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        assertThat(request1.hashCode()).isNotEqualTo(request3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request).isNotEqualTo(null);
        assertThat(request).isNotEqualTo(new Object());
    }

    @Test
    void testEqualsWithSameObject() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request).isEqualTo(request);
    }
}
