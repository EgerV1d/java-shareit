package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingRequestDto() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 7, 11, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 12, 10, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2026-07-11T10:00:00\"");
        assertThat(json).contains("\"end\":\"2026-07-12T10:00:00\"");
    }

    @Test
    void shouldDeserializeBookingRequestDto() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2026-07-11T10:00:00\",\"end\":\"2026-07-12T10:00:00\"}";

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 7, 11, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 7, 12, 10, 0));
    }

    @Test
    void shouldSerializeItemRequestDto() throws Exception {
        ItemResponseDto item = ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .build();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.of(2026, 7, 10, 10, 0))
                .items(List.of(item))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"requesterId\":1");
        assertThat(json).contains("\"items\"");
        assertThat(json).contains("\"name\":\"Drill\"");
    }

    @Test
    void shouldDeserializeItemRequestDto() throws Exception {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"requesterId\":1," +
                "\"created\":\"2026-07-10T10:00:00\",\"items\":[{\"id\":1,\"name\":\"Drill\",\"ownerId\":2}]}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getRequesterId()).isEqualTo(1L);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void shouldSerializeUserDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }
}
