package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto add(long ownerId, CreateItemDto itemDto) {
        log.info("Добавление предмета {}", itemDto);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userMapper.toUser(userService.getUser(ownerId)));

        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, long ownerId, UpdateItemDto itemDto) {
        log.info("Обновление предмета {}", itemDto);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(ownerId)) throw new NotFoundException("Owner not found");

        Item updatedItem = itemMapper.updateItemFields(item, itemDto);

        Item savedItem = itemRepository.save(updatedItem);

        log.info("Обновленный предмет  {}", itemDto);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public Collection<ItemDto> getItems(long ownerId) {
        log.info("Получение всех предметов по владельцу с id {}", ownerId);
        return itemRepository.findAllWithCommentsByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.info("Получение предмета с id {}", itemId);

        return itemRepository.findByIdWithComments(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public Collection<ItemDto> searchItems(String query) {

        return itemRepository.searchItems(query).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto addComment(long itemId, long authorId, CreateCommentDto dto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean canComment = bookingRepository.hasFinishedBooking(
                itemId,
                authorId,
                LocalDateTime.now()
        );

        if (!canComment) {
            throw new ValidationException("User has not finished booking this item");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }
}