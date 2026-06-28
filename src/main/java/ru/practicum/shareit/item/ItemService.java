package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllByOwner(Long ownerId);

    ItemDto findById(Long id, Long userId);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long itemId, Long ownerId, ItemDto itemDto);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, String text);
}
