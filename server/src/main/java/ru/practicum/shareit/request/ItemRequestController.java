package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody CreateItemRequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    List<ItemRequestDto> getItemRequestDto(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getUsersItemRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequestById(@PathVariable long requestId) {
        return requestService.getItemRequest(requestId);
    }
}