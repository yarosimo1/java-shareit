package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.SavedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserMapper {
    UserDto toUserDto(User user);

    SavedUserDto toSavedUserDto(User user);

    User toUser(UserDto userDto);

    User toUser(CreateUserDto userDto);

    User updateUserFields(User user, UpdateUserDto userDto);
}
