package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private long nextId = 1;

    public List<Item> findAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .toList();
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public List<Item> searchAvailable(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                    || item.getDescription().toLowerCase().contains(lowerText))
                .toList();
    }

    public boolean existsById(Long id) {
        return items.containsKey(id);
    }
}
