package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private CreateUserDto validCreateUser;
    private UpdateUserDto validUpdateUser;

    @BeforeEach
    public void setUp() {
        validCreateUser = new CreateUserDto();
        validCreateUser.setName("Иван");
        validCreateUser.setEmail("ivan@example.com");

        validUpdateUser = new UpdateUserDto();
        validUpdateUser.setName("Петр");
        validUpdateUser.setEmail("petr@example.com");
    }

    @Test
    public void postUser_whenValid_thenReturns200() throws Exception {
        when(userClient.addUser(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.ok("UserCreated"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("UserCreated"));
    }

    @Test
    public void postUser_whenInvalid_thenReturns400() throws Exception {
        CreateUserDto invalidUser = new CreateUserDto(); // имя и email пустые

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_whenValid_thenReturns200() throws Exception {
        when(userClient.updateUser(anyLong(), any(UpdateUserDto.class)))
                .thenReturn(ResponseEntity.ok("UserUpdated"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("UserUpdated"));
    }

    @Test
    public void updateUser_whenInvalidEmail_thenReturns400() throws Exception {
        UpdateUserDto invalidUpdate = new UpdateUserDto();
        invalidUpdate.setEmail("invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUser_whenCalled_thenReturns200() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok("UserFound"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("UserFound"));
    }

    @Test
    public void deleteUser_whenCalled_thenReturns200() throws Exception {
        when(userClient.deleteUserById(anyLong()))
                .thenReturn(ResponseEntity.ok("UserDeleted"));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("UserDeleted"));
    }
}