package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(UserServiceImpl.class)
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        // Маппинг User -> UserDto
        when(userMapper.toUserDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        });

        // Маппинг CreateUserDto -> User
        when(userMapper.toUser(any(CreateUserDto.class))).thenAnswer(invocation -> {
            CreateUserDto dto = invocation.getArgument(0);
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            return user;
        });

        // Обновление User по UpdateUserDto
        when(userMapper.updateUserFields(any(User.class), any(UpdateUserDto.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UpdateUserDto dto = invocation.getArgument(1);
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            return user;
        });
    }

    @Test
    void addUser_whenValid_thenSaved() {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@example.com");

        UserDto saved = userService.add(dto);

        assertNotNull(saved.getId());
        assertEquals("Иван", saved.getName());
        assertEquals("ivan@example.com", saved.getEmail());

        assertEquals(1, userRepository.count());
    }

    @Test
    void addUser_whenDuplicateEmail_thenThrows() {
        User existing = new User();
        existing.setName("Петр");
        existing.setEmail("ivan@example.com");
        userRepository.save(existing);

        CreateUserDto dto = new CreateUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@example.com");

        assertThrows(DuplicatedDataException.class, () -> userService.add(dto));
    }

    @Test
    void updateUser_whenValid_thenUpdated() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        user = userRepository.save(user);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("Иван Петров");
        updateDto.setEmail("ivan.p@example.com");

        UserDto updated = userService.update(user.getId(), updateDto);

        assertEquals("Иван Петров", updated.getName());
        assertEquals("ivan.p@example.com", updated.getEmail());
    }

    @Test
    void getUser_whenNotFound_thenThrows() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void deleteUser_whenExists_thenDeleted() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        user = userRepository.save(user);

        userService.delete(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
