package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest toEntity(String description, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requesterId(request.getRequester().getId())
                .created(request.getCreated())
                .build();
    }

    public ItemRequestDto toDtoWithItems(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = toDto(request);
        if (items != null) {
            List<ItemResponseDto> responseItems = items.stream()
                    .map(item -> ItemResponseDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .ownerId(item.getOwner().getId())
                            .build())
                    .toList();
            dto.setItems(responseItems);
        }

        return dto;
    }
}
