package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto testUser;

    @BeforeEach
    void setUp() {
        userService.findAll().forEach(u -> userService.delete(u.getId()));
        testUser = createUser("Requester", "requester@example.com");
    }

    @Test
    void shouldCreateRequest() {
        CreateItemRequestDto request = new CreateItemRequestDto();
        request.setDescription("Need a drill");

        ItemRequestDto result = requestService.create(testUser.getId(), request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getRequesterId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        CreateItemRequestDto request = new CreateItemRequestDto();
        request.setDescription("Need a drill");

        assertThrows(NotFoundException.class,
                () -> requestService.create(999L, request));
    }

    @Test
    void shouldGetUserRequests() {
        CreateItemRequestDto request1 = new CreateItemRequestDto();
        request1.setDescription("Request 1");
        requestService.create(testUser.getId(), request1);

        CreateItemRequestDto request2 = new CreateItemRequestDto();
        request2.setDescription("Request 2");
        requestService.create(testUser.getId(), request2);

        List<ItemRequestDto> requests = requestService.getUserRequests(testUser.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 2");
        assertThat(requests.get(1).getDescription()).isEqualTo("Request 1");
    }

    @Test
    void shouldGetAllRequests() {
        UserDto user1 = createUser("User1", "user1@example.com");
        UserDto user2 = createUser("User2", "user2@example.com");

        CreateItemRequestDto request = new CreateItemRequestDto();
        request.setDescription("Request from user2");
        requestService.create(user2.getId(), request);

        List<ItemRequestDto> requests = requestService.getAllRequests(user1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request from user2");
    }

    @Test
    void shouldGetRequestById() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("Need a drill");
        ItemRequestDto created = requestService.create(testUser.getId(), requestDto);

        UserDto owner = createUser("Owner", "owner@example.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(created.getId());
        itemService.create(owner.getId(), itemDto);

        ItemRequestDto result = requestService.getRequestById(testUser.getId(), created.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void shouldThrowExceptionWhenRequestNotFound() {
        UserDto user = createUser("User", "user@example.com");

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(user.getId(), 999L));
    }

    private UserDto createUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userService.create(userDto);
    }
}
