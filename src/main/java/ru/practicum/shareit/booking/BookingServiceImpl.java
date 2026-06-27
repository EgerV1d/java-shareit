package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto requestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (requestDto.getStart().isAfter(requestDto.getEnd()) ||
            requestDto.getStart().equals(requestDto.getEnd())) {
            throw new BadRequestException("Дата начала должна быть раньше даты окончания");
        }

        if (requestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала не может быть в прошлом");
        }

        Booking booking = bookingMapper.toEntity(requestDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        log.info("Создано бронирование: id={}, пользователь={}, вещь={}",
                saved.getId(), userId, item.getId());
        return bookingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessException("Подтвердить бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);
        log.info("Бронирование {} {}: id={}",
                bookingId, approved ? "подтверждено" : "отклонено", bookingId);
        return bookingMapper.toDto(updated);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessException("Просмотреть бронирование могут только автор или владелец вещи");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findAllByBooker(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL -> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookings = bookingRepository.findCurrentByBooker(userId, now);
            case PAST -> bookings = bookingRepository.findPastByBooker(userId, now);
            case FUTURE -> bookings = bookingRepository.findFutureByBooker(userId, now);
            case WAITING -> bookings = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByOwner(userId);
            case CURRENT -> bookings = bookingRepository.findCurrentByOwner(userId, now);
            case PAST -> bookings = bookingRepository.findPastByOwner(userId, now);
            case FUTURE -> bookings = bookingRepository.findFutureByOwner(userId, now);
            case WAITING -> bookings = bookingRepository.findByOwnerAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByOwnerAndStatus(userId, BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findAllByOwner(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}
