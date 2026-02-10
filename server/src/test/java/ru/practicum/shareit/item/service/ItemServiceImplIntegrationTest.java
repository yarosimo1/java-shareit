package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

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
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    private User testUser;
    private CreateItemDto createItemDto;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .build());

        UserDto savedUser = new UserDto();
        savedUser.setId(testUser.getId());
        savedUser.setName(testUser.getName());
        savedUser.setEmail(testUser.getEmail());

        // Мокаем UserService.getUser
        when(userService.getUser(testUser.getId()))
                .thenReturn(savedUser);

        // Создаем DTO для предмета
        createItemDto = new CreateItemDto();
        createItemDto.setName("Test Item");
        createItemDto.setDescription("Test Description");
        createItemDto.setAvailable(true);
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
}
