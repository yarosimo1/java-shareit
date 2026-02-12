package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private User owner;
    private User booker;
    private CreateItemDto createItemDto;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        testUser = userRepository.save(new User(null, "Test User", "testuser@example.com"));

        UserDto savedUser = new UserDto();
        savedUser.setId(testUser.getId());
        savedUser.setName(testUser.getName());
        savedUser.setEmail(testUser.getEmail());

        when(userService.getUser(testUser.getId())).thenReturn(savedUser);

        createItemDto = new CreateItemDto();
        createItemDto.setName("Test Item");
        createItemDto.setDescription("Test Description");
        createItemDto.setAvailable(true);

        item = itemRepository.save(new Item(null, owner, "item", "desc", true, null, List.of()));
    }

    @Test
    void addItem_ShouldSaveItem() {
        ItemDto savedItem = itemService.add(testUser.getId(), createItemDto);

        assertNotNull(savedItem.getId());
        assertEquals(createItemDto.getName(), savedItem.getName());
        assertEquals(createItemDto.getDescription(), savedItem.getDescription());
    }

    @Test
    void updateItem_ShouldUpdateFields() {
        ItemDto savedItem = itemService.add(testUser.getId(), createItemDto);

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Updated Name");
        updateItemDto.setDescription("Updated Description");
        updateItemDto.setAvailable(false);

        ItemDto updatedItem = itemService.update(savedItem.getId(), testUser.getId(), updateItemDto);

        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void updateItem_shouldThrowIfNotOwner() {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Fail Update");

        assertThrows(NotFoundException.class, () ->
                itemService.update(item.getId(), booker.getId(), updateItemDto));
    }

    @Test
    void getItem_ShouldReturnSavedItem() {
        ItemDto savedItem = itemService.add(testUser.getId(), createItemDto);

        ItemDto fetchedItem = itemService.getItem(savedItem.getId());

        assertEquals(savedItem.getId(), fetchedItem.getId());
        assertEquals(savedItem.getName(), fetchedItem.getName());
    }

    @Test
    void getItems_ShouldReturnAllUserItems() {
        itemService.add(testUser.getId(), createItemDto);

        Collection<ItemDto> items = itemService.getItems(testUser.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() {
        itemService.add(testUser.getId(), createItemDto);

        Collection<ItemDto> items = itemService.searchItems("test");

        assertFalse(items.isEmpty());
        assertEquals("Test Item", items.iterator().next().getName());
    }

    @Test
    void addComment_shouldSaveComment_whenUserHasFinishedBooking() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                booker,
                Status.APPROVED
        ));

        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("Great item!");

        CommentDto result = itemService.addComment(item.getId(), booker.getId(), dto);

        assertNotNull(result.getId());
        assertEquals("Great item!", result.getText());
        assertEquals(booker.getName(), result.getAuthorName());

        Comment saved = commentRepository.findById(result.getId()).orElseThrow();
        assertEquals("Great item!", saved.getText());
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenItemNotFound() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("Nice!");

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.addComment(999L, booker.getId(), dto));

        assertEquals("Item not found", ex.getMessage());
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenUserNotFound() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("Nice!");

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.addComment(item.getId(), 999L, dto));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void addComment_shouldThrowValidationException_whenBookingNotFinished() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("Bad comment");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.addComment(item.getId(), booker.getId(), dto));

        assertEquals("User has not finished booking this item", ex.getMessage());
    }

    @Test
    void addItem_withRequestId_shouldSetRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(testUser);
        itemRequest.setItems(List.of());
        itemRequest.setId(null);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Need a drill");

        ItemRequest request = itemRequestRepository.save(itemRequest);

        // DTO для нового предмета с requestId
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        dto.setRequestId(request.getId());

        ItemDto savedItem = itemService.add(testUser.getId(), dto);

        assertNotNull(savedItem.getId());
        assertEquals(dto.getName(), savedItem.getName());
        assertEquals(dto.getDescription(), savedItem.getDescription());

        // Проверяем, что в сущности item реально привязался request
        Item itemFromDb = itemRepository.findById(savedItem.getId()).orElseThrow();
        assertNotNull(itemFromDb.getRequest());
        assertEquals(request.getId(), itemFromDb.getRequest().getId());
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemDoesNotExist() {
        long nonExistentItemId = 999L; // ID которого нет в БД
        long ownerId = testUser.getId(); // может быть любой существующий пользователь

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("Updated Name");
        dto.setDescription("Updated Description");
        dto.setAvailable(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.update(nonExistentItemId, ownerId, dto));

        assertEquals("Item not found", ex.getMessage());
    }

}
