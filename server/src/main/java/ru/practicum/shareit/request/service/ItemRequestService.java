package ru.practicum.shareit.request.service;

import jakarta.validation.Valid;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(long userId, @Valid CreateItemRequestDto requestDto);

    List<ItemRequestDto> getUsersItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId);

    ItemRequestDto getItemRequest(long itemRequestId);
}