package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        NotFoundException ex = new NotFoundException("Пользователь не найден");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Пользователь не найден");
    }

    @Test
    void handleDuplicateEmail_shouldReturn409() {
        DuplicateEmailException ex = new DuplicateEmailException("Пользователь с таким email уже существует");
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmail(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Пользователь с таким email уже существует");
    }

    @Test
    void handleAccess_shouldReturn403() {
        AccessException ex = new AccessException("Доступ запрещен");
        ResponseEntity<ErrorResponse> response = handler.handleAccess(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Доступ запрещен");
    }

    @Test
    void handleBookingNotAvailable_shouldReturn400() {
        BookingNotAvailableException ex = new BookingNotAvailableException("Вещь недоступна для бронирования");
        ResponseEntity<ErrorResponse> response = handler.handleBookingNotAvailable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Вещь недоступна для бронирования");
    }

    @Test
    void handleInvalidBookingDates_shouldReturn400() {
        InvalidBookingDatesException ex = new InvalidBookingDatesException("Дата начала должна быть раньше даты окончания");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidBookingDates(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Дата начала должна быть раньше даты окончания");
    }

    @Test
    void handleBookingStatus_shouldReturn400() {
        BookingStatusException ex = new BookingStatusException("Бронирование уже подтверждено или отклонено");
        ResponseEntity<ErrorResponse> response = handler.handleBookingStatus(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Бронирование уже подтверждено или отклонено");
    }

    @Test
    void handleUnknownBookingState_shouldReturn400() {
        UnknownBookingStateException ex = new UnknownBookingStateException("Неизвестное состояние: INVALID");
        ResponseEntity<ErrorResponse> response = handler.handleUnknownBookingState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Неизвестное состояние: INVALID");
    }

    @Test
    void handleCommentNotAllowed_shouldReturn400() {
        CommentNotAllowedException ex = new CommentNotAllowedException("Пользователь не брал эту вещь в аренду");
        ResponseEntity<ErrorResponse> response = handler.handleCommentNotAllowed(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Пользователь не брал эту вещь в аренду");
    }
}
