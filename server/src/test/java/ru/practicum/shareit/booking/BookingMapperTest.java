package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapper();

    @Test
    void toEntity_shouldMapCorrectly() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 7, 12, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 13, 10, 0));

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");

        Booking booking = mapper.toEntity(dto, item, booker);

        assertThat(booking.getStart()).isEqualTo(dto.getStart());
        assertThat(booking.getEnd()).isEqualTo(dto.getEnd());
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
    }

    @Test
    void toDto_shouldMapCorrectly() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2026, 7, 12, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 7, 13, 10, 0));
        booking.setStatus(BookingStatus.WAITING);

        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booking.setBooker(booker);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        booking.setItem(item);

        BookingDto dto = mapper.toDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getName()).isEqualTo("Booker");
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getName()).isEqualTo("Test Item");
    }

    @Test
    void toDto_shouldReturnNullWhenBookingIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }
}
