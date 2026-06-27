package ru.practicum.shareit.exception;

public class NoEmailException extends RuntimeException {
    public NoEmailException(String message) {
        super(message);
    }
}
