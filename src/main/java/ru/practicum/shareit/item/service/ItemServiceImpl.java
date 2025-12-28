package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto add(long ownerId, CreateItemDto itemDto) {
        log.info("Добавление предмета {}", itemDto);
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userService.getUser(ownerId).getId());

        return itemMapper.toItemDto(
                itemRepository.save(item));
    }

    @Override
    public ItemDto update(long itemId, long ownerId, UpdateItemDto itemDto) {
        log.info("Обновление предмета {}", itemDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(ownerId)) throw new NotFoundException("Owner not found");

        Item updatedItem = itemMapper.updateItemFields(item, itemDto);

        Item savedItem = itemRepository.update(updatedItem);

        log.info("Обновленный предмет  {}", itemDto);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public Collection<ItemDto> getItems(long ownerId) {
        log.info("Получение всех предметов по владельцу с id {}", ownerId);
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.info("Получение предмета с id", itemId);
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public Collection<ItemDto> searchItems(String query) {
        return itemRepository.searchItems(query).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
