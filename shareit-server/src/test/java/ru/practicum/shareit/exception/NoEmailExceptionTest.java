package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NoEmailExceptionTest {
    @Test
    void shouldCreateNoEmailException() {
        String message = "Email не может быть пустым";
        NoEmailException exception = new NoEmailException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldCreateNoEmailExceptionWithDifferentMessage() {
        String message = "Email is required";
        NoEmailException exception = new NoEmailException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getMessage()).isNotEqualTo("Email не может быть пустым");
    }

    @Test
    void shouldBeInstanceOfRuntimeException() {
        NoEmailException exception = new NoEmailException("Email is required");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
