package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingRequestDto requestDto);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findAllByBooker(Long userId, BookingState state);

    List<BookingDto> findAllByOwner(Long userId, BookingState state);
}
