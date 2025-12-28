package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @Valid @RequestBody CreateItemDto itemDto) {
        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                              @Valid @RequestBody UpdateItemDto itemDto) {
        return itemService.update(itemId, ownerId, itemDto);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text", required = false)
                                               String text) {
        return itemService.searchItems(text);
    }
}