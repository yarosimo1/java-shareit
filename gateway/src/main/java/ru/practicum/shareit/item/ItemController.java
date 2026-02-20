package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                          @Valid @RequestBody CreateItemDto itemDto) {
        return itemClient.addItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long authorId,
                                             @Valid @RequestBody CreateCommentDto commentDto) {
        return itemClient.addComment(itemId, authorId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody UpdateItemDto itemDto) {
        return itemClient.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemClient.getItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") long itemId) {
        return itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @RequestParam(name = "text", required = false) String text) {
        return itemClient.searchItems(ownerId, text);
    }
}