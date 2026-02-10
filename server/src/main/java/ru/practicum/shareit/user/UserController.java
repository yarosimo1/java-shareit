package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto postUser(@RequestBody CreateUserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto patchUser(@PathVariable("userId") long id,
                             @RequestBody(required = false) UpdateUserDto userDto) {
        return userService.update(id, userDto);
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        userService.delete(id);
    }
}