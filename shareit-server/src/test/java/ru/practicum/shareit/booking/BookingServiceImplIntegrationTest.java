package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldCreateBooking() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");

        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.create(booker.getId(), request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldThrowExceptionWhenItemNotAvailable() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");

        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", false);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(BookingNotAvailableException.class,
                () -> bookingService.create(booker.getId(), request));
    }

    @Test
    void shouldThrowExceptionWhenOwnerBooksOwnItem() {
        UserDto owner = createUser("Owner", "owner@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(owner.getId(), request));
    }

    @Test
    void shouldApproveBooking() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), request);
        BookingDto approved = bookingService.approve(created.getId(), owner.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldThrowExceptionWhenApproveNotOwner() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        UserDto another = createUser("Another", "another@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), request);

        assertThrows(AccessException.class,
                () -> bookingService.approve(created.getId(), another.getId(), true));
    }

    @Test
    void shouldThrowExceptionWhenBookingAlreadyApproved() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), request);
        bookingService.approve(created.getId(), owner.getId(), true);

        assertThrows(BookingStatusException.class,
                () -> bookingService.approve(created.getId(), owner.getId(), true));
    }

    @Test
    void shouldFindBookingById() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), request);
        BookingDto found = bookingService.findById(created.getId(), booker.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void shouldThrowExceptionWhenFindBookingNotAuthorized() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        UserDto another = createUser("Another", "another@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), request);

        assertThrows(AccessException.class,
                () -> bookingService.findById(created.getId(), another.getId()));
    }

    @Test
    void shouldFindAllByBooker() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(booker.getId(), request);

        List<BookingDto> bookings = bookingService.findAllByBooker(booker.getId(), BookingState.ALL);

        assertThat(bookings).isNotEmpty();
    }

    @Test
    void shouldThrowExceptionWhenStartDateInPast() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().minusDays(1));  // дата в прошлом
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.create(booker.getId(), request));
    }

    @Test
    void shouldThrowExceptionWhenStartDateAfterEndDate() {
        UserDto owner = createUser("Owner", "owner@example.com");
        UserDto booker = createUser("Booker", "booker@example.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Description", true);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(2));
        request.setEnd(LocalDateTime.now().plusDays(1));  // start после end

        assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.create(booker.getId(), request));
    }

    private UserDto createUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userService.create(userDto);
    }

    private ItemDto createItem(Long ownerId, String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemService.create(ownerId, itemDto);
    }
}
