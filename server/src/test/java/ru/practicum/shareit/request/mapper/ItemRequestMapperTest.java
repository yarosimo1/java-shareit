package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = ItemRequestMapperImpl.INSTANCE;

    @Test
    public void toItemRequestDto_shouldMapFields() {
        User requestor = new User(1L, "Alice", "alice@mail.com");
        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(requestor.getId(), dto.getRequestorId());
    }

    @Test
    public void toItemRequest_shouldMapFields() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("Need a drill");

        ItemRequest request = mapper.toItemRequest(dto);

        assertEquals(dto.getDescription(), request.getDescription());
        assertNull(request.getId());
        assertNull(request.getRequestor());
    }

    @Test
    public void toItemRequestDto_shouldMapItemsList() {
        User requestor = new User(1L, "Alice", "alice@mail.com");
        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);

        // Добавляем несколько Item
        Item item1 = new Item();
        item1.setId(101L);
        item1.setName("Drill");

        Item item2 = new Item();
        item2.setId(102L);
        item2.setName("Hammer");

        request.setItems(List.of(item1, item2));

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());
        assertEquals("Drill", dto.getItems().get(0).getName());
        assertEquals(101L, dto.getItems().get(0).getId());
        assertEquals("Hammer", dto.getItems().get(1).getName());
        assertEquals(102L, dto.getItems().get(1).getId());
    }

    @Test
    public void toItemRequestDto_shouldReturnNullRequestorId_whenRequestorIsNull() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test request");
        request.setRequestor(null); // << ветка requestor == null
        request.setCreated(java.time.LocalDateTime.now());

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        assertNotNull(dto);
        assertNull(dto.getRequestorId()); // проверяем, что null возвращается
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
    }


    @Test
    public void toItemRequestDto_shouldReturnNullForNull() {
        assertNull(mapper.toItemRequestDto(null));
    }

    @Test
    public void toItemRequest_shouldReturnNullForNull() {
        assertNull(mapper.toItemRequest(null));
    }

    @Test
    public void requestDtoRequestorId_shouldReturnNullForNullRequestor() {
        ItemRequest request = new ItemRequest();
        request.setRequestor(null);
        assertNull(mapper.toItemRequestDto(request).getRequestorId());
    }
}
