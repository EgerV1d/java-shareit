package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private final Map<BookingState, BookingFetchStrategy> bookerStrategies = new EnumMap<>(BookingState.class);
    private final Map<BookingState, BookingFetchStrategy> ownerStrategies = new EnumMap<>(BookingState.class);

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
        initializeStrategies();
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto requestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new BookingNotAvailableException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (requestDto.getStart().isAfter(requestDto.getEnd()) ||
            requestDto.getStart().equals(requestDto.getEnd())) {
            throw new InvalidBookingDatesException("Дата начала должна быть раньше даты окончания");
        }

        if (requestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingDatesException("Дата начала не может быть в прошлом");
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
            throw new BookingStatusException("Бронирование уже подтверждено или отклонено");
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

        BookingFetchStrategy strategy = bookerStrategies.get(state);
        if (strategy == null) {
            throw new UnknownBookingStateException("Неизвестное состояние: " + state);
        }

        List<Booking> bookings = strategy.fetch(userId, LocalDateTime.now());
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        BookingFetchStrategy strategy = ownerStrategies.get(state);
        if (strategy == null) {
            throw new UnknownBookingStateException("Неизвестное состояние: " + state);
        }

        List<Booking> bookings = strategy.fetch(userId, LocalDateTime.now());
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    private void initializeStrategies() {
        bookerStrategies.put(BookingState.ALL, ((userId, now) ->
                bookingRepository.findByBookerIdOrderByStartDesc(userId)));
        bookerStrategies.put(BookingState.CURRENT, bookingRepository::findCurrentByBooker);
        bookerStrategies.put(BookingState.PAST, bookingRepository::findPastByBooker);
        bookerStrategies.put(BookingState.FUTURE, bookingRepository::findFutureByBooker);
        bookerStrategies.put(BookingState.WAITING, ((userId, now) ->
                bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)));
        bookerStrategies.put(BookingState.REJECTED, ((userId, now) ->
                bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)));

        ownerStrategies.put(BookingState.ALL, ((userId, now) ->
                bookingRepository.findAllByOwner(userId)));
        ownerStrategies.put(BookingState.CURRENT, bookingRepository::findCurrentByOwner);
        ownerStrategies.put(BookingState.PAST, bookingRepository::findPastByOwner);
        ownerStrategies.put(BookingState.FUTURE, bookingRepository::findFutureByOwner);
        ownerStrategies.put(BookingState.WAITING, (userId, now) ->
                bookingRepository.findByOwnerAndStatus(userId, BookingStatus.WAITING));
        ownerStrategies.put(BookingState.REJECTED, ((userId, now) ->
                bookingRepository.findByOwnerAndStatus(userId, BookingStatus.REJECTED)));
    }
}
