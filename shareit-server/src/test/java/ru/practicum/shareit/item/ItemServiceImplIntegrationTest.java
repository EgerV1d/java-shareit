package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    private UserDto testOwner;
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        userService.findAll().forEach(u -> userService.delete(u.getId()));

        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        testOwner = userService.create(owner);

        UserDto user = new UserDto();
        user.setName("User");
        user.setEmail("user@example.com");
        testUser = userService.create(user);
    }

    @Test
    void shouldCreateItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto result = itemService.create(testOwner.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(999L, itemDto));
    }

    @Test
    void shouldFindItemById() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(testOwner.getId(), itemDto);

        ItemDto found = itemService.findById(createdItem.getId(), testOwner.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(createdItem.getId());
        assertThat(found.getName()).isEqualTo("Test Item");
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.findById(999L, 1L));
    }

    @Test
    void shouldUpdateItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Old Name");
        itemDto.setDescription("Old Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(testOwner.getId(), itemDto);

        ItemDto update = new ItemDto();
        update.setName("New Name");
        update.setDescription("New Description");
        update.setAvailable(false);

        ItemDto result = itemService.update(createdItem.getId(), testOwner.getId(), update);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenUpdateNotOwner() {
        UserDto anotherUser = new UserDto();
        anotherUser.setName("Another");
        anotherUser.setEmail("another@example.com");
        UserDto createdAnother = userService.create(anotherUser);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(testOwner.getId(), itemDto);

        ItemDto update = new ItemDto();
        update.setName("New Name");

        assertThrows(AccessException.class, () ->
                itemService.update(createdItem.getId(), createdAnother.getId(), update));
    }

    @Test
    void shouldThrowExceptionWhenCommentWithoutBooking() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(testOwner.getId(), itemDto);

        assertThrows(CommentNotAllowedException.class, () ->
                itemService.addComment(createdItem.getId(), testUser.getId(), "Great item!"));
    }

    @Test
    void shouldSearchItemsByText() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemService.create(testOwner.getId(), itemDto);

        List<ItemDto> results = itemService.search("drill");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).contains("Drill");
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        List<ItemDto> results = itemService.search("");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsNull() {
        List<ItemDto> results = itemService.search(null);
        assertThat(results).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() {
        List<ItemDto> results = itemService.search("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldFindAllByOwner() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemService.create(testOwner.getId(), itemDto);

        List<ItemDto> items = itemService.findAllByOwner(testOwner.getId());

        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getName()).isEqualTo("Test Item");
    }

    @Test
    void shouldThrowExceptionWhenFindByOwnerNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.findAllByOwner(999L));
    }
}
