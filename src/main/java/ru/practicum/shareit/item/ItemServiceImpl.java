package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> findAllByOwner(Long ownerId) {
        checkUserExists(ownerId);
        return itemRepository.findAllByOwner(ownerId).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto findById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        checkUserExists(ownerId);
        Item item = itemMapper.toEntity(itemDto, ownerId);
        Item saved = itemRepository.save(item);
        log.info("Создана вещь: id={}, name={}, owner={}", saved.getId(), saved.getName(), ownerId);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) throws AccessException {
        checkUserExists(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().equals(ownerId)) {
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
    public List<ItemDto> search(String text) {
        return itemRepository.searchAvailable(text).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
