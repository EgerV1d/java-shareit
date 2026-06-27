package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByOwner(Long ownerId) {
        checkUserExists(ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream()
                .map(item -> addBookingAndComments(item, ownerId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            return addBookingAndComments(item, userId);
        }
        ItemDto dto = itemMapper.toDto(item);
        dto.setComments(getCommentsForItem(id));
        return dto;
    }

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemMapper.toEntity(itemDto, owner);
        Item saved = itemRepository.save(item);
        log.info("Создана вещь: id={}, name={}, owner={}", saved.getId(), saved.getName(), ownerId);
        return itemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) {
        checkUserExists(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updated = itemRepository.save(item);
        log.info("Обновлена вещь: id={}, owner={}", updated.getId(), ownerId);
        return itemMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailable(text).stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toDto(item);
                    dto.setComments(getCommentsForItem(item.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, String text) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        boolean hasBooked = bookingRepository.existsApprovedBookingByUser(itemId, userId, LocalDateTime.now());
        if (!hasBooked) {
            throw new BadRequestException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = commentMapper.toEntity(text, item, author);
        Comment saved = commentRepository.save(comment);
        log.info("Добавлен комментарий: id={}, пользователь={}, вещь={}", saved.getId(), userId, itemId);

        ItemDto dto = itemMapper.toDto(item);
        dto.setComments(getCommentsForItem(itemId));
        return commentMapper.toDto(saved);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private ItemDto addBookingAndComments(Item item, Long usersId) {
        ItemDto dto = itemMapper.toDto(item);
        List<Booking> lastBookings = bookingRepository.findLastApprovedBookingsByItem(item.getId());

        if (!lastBookings.isEmpty()) {
            dto.setLastBooking(convertToBookingDto(lastBookings.get(0)));
        }
        dto.setComments(getCommentsForItem(item.getId()));
        return dto;
    }

    private BookingDto convertToBookingDto(Booking booking) {
        return bookingMapper.toDto(booking);
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toDto)
                .toList();
    }
}
