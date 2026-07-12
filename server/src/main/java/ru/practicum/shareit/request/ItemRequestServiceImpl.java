package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, CreateItemRequestDto requestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = mapper.toEntity(requestDto.getDescription(), requester);
        ItemRequest saved = itemRequestRepository.save(request);

        log.info("Создан запрос вещи: id={}, пользователь={}", saved.getId(), userId);
        return mapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return enrichWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, sort);
        return enrichWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        return mapper.toDtoWithItems(request, items);
    }

    private List<ItemRequestDto> enrichWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> allItems = itemRepository.findByRequestIdIn(requestIds);
        Map<Long, List<Item>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    return mapper.toDtoWithItems(request, items);
                })
                .toList();
    }
}
