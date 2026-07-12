package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService.findAll().forEach(user -> userService.delete(user.getId()));
    }

    @Test
    void shouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        UserDto result = userService.create(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailDuplicate() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("User 1");
        userDto1.setEmail("duplicate@example.com");
        userService.create(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("User 2");
        userDto2.setEmail("duplicate@example.com");

        assertThrows(DuplicateEmailException.class, () -> userService.create(userDto2));
    }

    @Test
    void shouldFindUserById() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto created = userService.create(userDto);

        UserDto found = userService.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Test User");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Old Name");
        userDto.setEmail("old@example.com");
        UserDto created = userService.create(userDto);

        UserDto update = new UserDto();
        update.setName("New Name");

        UserDto result = userService.update(created.getId(), update);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("old@example.com");
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto created = userService.create(userDto);

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.findById(created.getId()));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("");

        assertThrows(NoEmailException.class,
                () -> userService.create(userDto));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail(null);

        assertThrows(NoEmailException.class,
                () -> userService.create(userDto));
    }

    @Test
    void shouldUpdateOnlyName() {
        UserDto userDto = new UserDto();
        userDto.setName("Old Name");
        userDto.setEmail("test@example.com");
        UserDto created = userService.create(userDto);

        UserDto update = new UserDto();
        update.setName("New Name");

        UserDto result = userService.update(created.getId(), update);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldUpdateOnlyEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("old@example.com");
        UserDto created = userService.create(userDto);

        UserDto update = new UserDto();
        update.setEmail("new@example.com");

        UserDto result = userService.update(created.getId(), update);

        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotFound() {
        assertThrows(NotFoundException.class,
                () -> userService.delete(999L));
    }

    @Test
    void shouldFindAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userService.create(user2);

        List<UserDto> users = userService.findAll();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }
}
