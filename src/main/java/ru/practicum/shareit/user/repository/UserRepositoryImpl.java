package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
       return users.values().stream()
                .filter(user ->
                        user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User save(User user) {
        log.info("Сохранение пользователя {}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь сохранен {}", user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        log.info("Обновление пользователя {}", user);

        if (!users.containsKey(user.getId())) {
            return Optional.empty();
        }

        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public void delete(long userId) {
        log.info("Удаление пользователя {}", userId);
        if (users.get(userId) != null) return;
        users.remove(userId);
    }

    @Override
    public Optional<User>  findById(long userId) {
        return Optional.ofNullable(
                users.get(userId));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
