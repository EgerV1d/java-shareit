package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccess(AccessException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingNotAvailable(BookingNotAvailableException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidBookingDatesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBookingDates(InvalidBookingDatesException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // или CONFLICT (409), если статус конфликтует
    public ErrorResponse handleBookingStatus(BookingStatusException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnknownBookingStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownBookingState(UnknownBookingStateException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CommentNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentNotAllowed(CommentNotAllowedException e) {
        return new ErrorResponse(e.getMessage());
    }
}
