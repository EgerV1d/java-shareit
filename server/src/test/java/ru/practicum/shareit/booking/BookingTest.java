package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookingTest {
    @Test
    void shouldCreateBooking() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStart()).isNotNull();
        assertThat(booking.getEnd()).isNotNull();
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldSetAllFields() {
        Booking booking = new Booking();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User booker = new User();
        booker.setId(1L);
        Item item = new Item();
        item.setId(1L);

        booking.setId(2L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        assertThat(booking.getId()).isEqualTo(2L);
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testEqualsAndHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        Booking booking3 = new Booking();
        booking3.setId(2L);

        assertThat(booking1).isEqualTo(booking2);
        assertThat(booking1).isNotEqualTo(booking3);
        assertThat(booking1.hashCode()).isEqualTo(booking2.hashCode());
        assertThat(booking1.hashCode()).isNotEqualTo(booking3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking).isNotEqualTo(null);
        assertThat(booking).isNotEqualTo(new Object());
    }

    @Test
    void testEqualsWithSameObject() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking).isEqualTo(booking);
    }
}
