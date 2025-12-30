package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(CreateUserDto user);

    UserDto update(long userId, UpdateUserDto user);

    Collection<UserDto> getUsers();

    UserDto getUser(long userId);

    void delete(long userId);
}
