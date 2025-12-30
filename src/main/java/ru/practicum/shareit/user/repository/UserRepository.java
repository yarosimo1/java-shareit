package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    User save(User user);

    Optional<User> update(User user);

    void delete(long userId);

    Optional<User> findById(long userId);

    Optional<User> findUserByEmail(String email);
}
