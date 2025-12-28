package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(CreateUserDto userDto) {
        log.info("Добавление пользователя {}", userDto);
        userRepository.findUserByEmail(userDto.getEmail()).ifPresent(user -> {
                    throw new DuplicatedDataException("This email addres is already used");});

        return userMapper.toUserDto(
                userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        log.info("Обновление пользователя {}", userDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.findUserByEmail(userDto.getEmail()).ifPresent(user1 -> {
            throw new DuplicatedDataException("This email addres is already used");});

        User updatedUser = userMapper.updateUserFields(user, userDto);

        User savedUser = userRepository.update(updatedUser)
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.info("Обновленный пользователь {}", userDto);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public Collection<UserDto> getUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(long userId) {
        log.info("Получение пользователя с id {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));
    }

    @Override
    public void delete(long userId) {
        log.info("Удаление пользователя");
        userRepository.delete(userId);
    }
}
