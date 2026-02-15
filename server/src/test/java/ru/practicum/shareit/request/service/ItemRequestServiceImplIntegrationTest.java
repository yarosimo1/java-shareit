package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRequestServiceImplIntegrationTest {

    CreateItemRequestDto hammer;
    CreateItemRequestDto drill;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    private long userId;
    private long anotherUserId;

    @BeforeEach
    public void setUp() {
        // Создаём первого пользователя
        CreateUserDto user = new CreateUserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userId = userService.add(user).getId();

        // Создаём второго пользователя для проверки getAllItemRequests
        CreateUserDto anotherUser = new CreateUserDto();
        anotherUser.setName("Other User");
        anotherUser.setEmail("other@example.com");
        anotherUserId = userService.add(anotherUser).getId();

        hammer = new CreateItemRequestDto();
        drill = new CreateItemRequestDto();
        hammer.setDescription("Нужен молоток");
        drill.setDescription("Нужна дрель");
    }

    @Test
    public void testAddItemRequest() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("Нужна дрель");

        ItemRequestDto savedRequest = itemRequestService.add(userId, requestDto);

        assertNotNull(savedRequest.getId(), "ID запроса должен быть заполнен");
        assertEquals("Нужна дрель", savedRequest.getDescription());
        assertNotNull(savedRequest.getCreated(), "Время создания должно быть заполнено");
    }

    @Test
    public void testGetUsersItemRequests() {
        // Добавляем несколько запросов для одного пользователя
        itemRequestService.add(userId, hammer);
        itemRequestService.add(userId, drill);

        List<ItemRequestDto> requests = itemRequestService.getUsersItemRequests(userId);

        assertEquals(2, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Нужна дрель")));
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Нужен молоток")));
    }

    @Test
    public void testGetAllItemRequests() {
        // Другой пользователь создаёт запрос
        itemRequestService.add(anotherUserId, hammer);

        // Наш пользователь должен видеть запрос другого
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(userId);

        assertEquals(1, requests.size());
        assertEquals("Нужен молоток", requests.get(0).getDescription());
    }

    @Test
    public void testGetItemRequest() {
        // Добавляем запрос и проверяем его получение по ID
        ItemRequestDto savedRequest = itemRequestService.add(userId, drill);

        ItemRequestDto foundRequest = itemRequestService.getItemRequest(savedRequest.getId());

        assertEquals(savedRequest.getId(), foundRequest.getId());
        assertEquals("Нужна дрель", foundRequest.getDescription());
    }

    @Test
    public void testMultipleUsersRequestsIsolation() {
        // Пользователь 1 добавляет запрос
        itemRequestService.add(userId, drill);

        // Пользователь 2 добавляет запрос
        itemRequestService.add(anotherUserId, hammer);

        // Пользователь 1 видит только свой запрос через getUsersItemRequests
        List<ItemRequestDto> userRequests = itemRequestService.getUsersItemRequests(userId);
        assertEquals(1, userRequests.size());
        assertEquals("Нужна дрель", userRequests.get(0).getDescription());

        // Пользователь 1 видит запросы других через getAllItemRequests
        List<ItemRequestDto> allRequests = itemRequestService.getAllItemRequests(userId);
        assertEquals(1, allRequests.size());
        assertEquals("Нужен молоток", allRequests.get(0).getDescription());
    }
}
