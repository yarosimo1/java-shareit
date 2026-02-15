package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    public void toUserDto_shouldMapFields() {
        User user = new User(1L, "Alice", "alice@mail.com");

        UserDto dto = mapper.toUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void toUser_fromCreateUserDto_shouldMapFields() {
        CreateUserDto createDto = new CreateUserDto();
        createDto.setName("Bob");
        createDto.setEmail("bob@mail.com");

        User user = mapper.toUser(createDto);

        assertNull(user.getId()); // CreateUserDto не содержит id
        assertEquals(createDto.getName(), user.getName());
        assertEquals(createDto.getEmail(), user.getEmail());
    }

    @Test
    public void toUser_fromUserDto_shouldMapFields() {
        UserDto dto = new UserDto();
        dto.setId(5L);
        dto.setName("Charlie");
        dto.setEmail("charlie@mail.com");

        User user = mapper.toUser(dto);

        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    public void updateUserFields_shouldUpdateNonNullFields() {
        User user = new User(1L, "Old Name", "old@mail.com");

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("New Name");
        updateDto.setEmail(null);

        mapper.updateUserFields(user, updateDto);

        assertEquals("New Name", user.getName());
        assertEquals("old@mail.com", user.getEmail()); // email не изменился
    }

    @Test
    public void updateUserFields_shouldUpdateEmailIfNotNull() {
        User user = new User(1L, "Name", "old@mail.com");

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setEmail("new@mail.com");

        mapper.updateUserFields(user, updateDto);

        assertEquals("Name", user.getName()); // name не изменился
        assertEquals("new@mail.com", user.getEmail());
    }

    @Test
    public void toUserDto_shouldReturnNull_whenUserIsNull() {
        UserDto dto = mapper.toUserDto(null);

        assertNull(dto);
    }

    @Test
    public void toUser_fromCreateUserDto_shouldReturnNull_whenDtoIsNull() {
        User user = mapper.toUser((CreateUserDto) null);

        assertNull(user);
    }

    @Test
    public void toUser_fromUserDto_shouldReturnNull_whenDtoIsNull() {
        User user = mapper.toUser((UserDto) null);

        assertNull(user);
    }

}