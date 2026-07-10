package ru.practicum.shareit.exception;

public class InvalidBookingDatesException extends RuntimeException {
    public InvalidBookingDatesException(String message) {
        super(message);
    }
}
