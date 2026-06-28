package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new NoEmailException("Email не может быть пустым");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }

        User user = userMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        log.info("Создан пользователь: id={}, email={}", saved.getId(), saved.getEmail());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }

        User updated = userRepository.save(user);
        log.info("Обновлен пользователь: id={}", updated.getId());
        return userMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
        log.info("Удалён пользователь: id={}", id);
    }
}
