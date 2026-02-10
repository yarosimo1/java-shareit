package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public ItemRequestDto add(long userId, CreateItemRequestDto requestDto) {
        log.info("Добавление запроса");

        User user = userMapper.toUser(userService.getUser(userId));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        return itemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUsersItemRequests(long userId) {
        log.info("Получение всех запросов пользователя");
        return requestRepository.getAllByUserId(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        log.info("Получение всех запросов пользователей");
        return requestRepository.findAllOtherUsersRequests(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequest(long itemRequestId) {
        log.info("Получение запроса");
        return itemRequestMapper.toItemRequestDto(requestRepository.findItemRequestById(itemRequestId));
    }
}