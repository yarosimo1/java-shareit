package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.SavedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public SavedUserDto toSavedUserDto(User user) {
        if (user == null) return null;
        return new SavedUserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public User toUser(UserDto userDto) {
        if (userDto == null) return null;
        return new User(
                userDto.getName(),
                userDto.getEmail()
        );
    }

    @Override
    public User toUser(CreateUserDto userDto) {
        if (userDto == null) return null;
        return new User(
                userDto.getName(),
                userDto.getEmail()
        );
    }

    @Override
    public User updateUserFields(User user, UpdateUserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return user;
    }
}
