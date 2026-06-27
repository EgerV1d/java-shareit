package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    public boolean existsDuplicateWithEmail(String email, long currentUserId) {
        return users.values().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
