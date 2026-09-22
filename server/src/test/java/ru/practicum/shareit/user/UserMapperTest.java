package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toDto_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        UserDto dto = mapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toDto_shouldReturnNullWhenUserIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_shouldMapCorrectly() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        User user = mapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
