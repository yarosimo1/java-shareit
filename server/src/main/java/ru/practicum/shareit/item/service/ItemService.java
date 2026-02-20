package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(long ownerId, CreateItemDto item);

    ItemDto update(long itemId, long ownerId, UpdateItemDto item);

    Collection<ItemDto> getItems(long ownerId);

    ItemDto getItem(long itemId);

    Collection<ItemDto> searchItems(String query);

    CommentDto addComment(long itemId, long authorId, CreateCommentDto commentDto);
}
