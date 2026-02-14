package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void postUser_returnsUser() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@example.com");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Иван");
        response.setEmail("ivan@example.com");

        when(userService.add(any(CreateUserDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    public void getUser_returnsUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        when(userService.getUser(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    public void patchUser_updatesUser() throws Exception {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("Петр");

        UserDto updated = new UserDto();
        updated.setId(1L);
        updated.setName("Петр");
        updated.setEmail("ivan@example.com");

        when(userService.update(anyLong(), any(UpdateUserDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Петр"));
    }

    @Test
    public void deleteUser_shouldReturn200() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}
