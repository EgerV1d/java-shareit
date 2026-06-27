package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @NotBlank(message = "email не может быть пустым")
    @Email(message = "email должен быть корректным")
    private String email;
}
