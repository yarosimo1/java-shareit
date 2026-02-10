package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    ResponseEntity<Object> postUser(@Valid @RequestBody CreateUserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping(path = "/{userId}")
    ResponseEntity<Object> updateUser(@PathVariable("userId") long id,
                                      @Valid @RequestBody UpdateUserDto userDto) {
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{userId}")
    ResponseEntity<Object> getUser(@PathVariable("userId") long id) {
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<Object> deleteUser(@PathVariable("userId") long id) {
        return userClient.deleteUserById(id);
    }
}