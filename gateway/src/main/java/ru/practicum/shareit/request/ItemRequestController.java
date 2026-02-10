package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody CreateItemRequestDto requestDto) {
        return itemRequestClient.addRequest(userId, requestDto);
    }

    @GetMapping
    ResponseEntity<Object> getItemRequestDto(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUsersItemRequests(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequestById(@PathVariable long requestId) {
        return itemRequestClient.getItemRequest(requestId);
    }
}