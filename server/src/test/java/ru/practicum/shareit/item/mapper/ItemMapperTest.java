package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapperImpl mapper;

    @MockBean
    private CommentMapper commentMapper;

    @Test
    public void toItemDto_shouldMapFieldsAndRequestId() {
        User owner = new User(1L, "John", "john@mail.com");
        ItemRequest request = new ItemRequest();
        request.setId(100L);

        Item item = new Item(10L, owner, "Drill", "Power drill", true, request, null);

        ItemDto dto = mapper.toItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(request.getId(), dto.getRequestId());
        assertNull(dto.getComments());
    }

    @Test
    public void toItemDto_withComments_shouldMapComments() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Good")
                .created(LocalDateTime.now())
                .author(new User(2L, "Alice", "alice@mail.com"))
                .build();

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good");
        commentDto.setAuthorName("Alice");
        commentDto.setCreated(comment.getCreated());

        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        Item item = new Item();
        item.setComments(List.of(comment));

        ItemDto dto = mapper.toItemDto(item);

        assertNotNull(dto.getComments());
        assertEquals(1, dto.getComments().size());
        assertEquals("Alice", dto.getComments().get(0).getAuthorName());
        assertEquals("Good", dto.getComments().get(0).getText());
    }

    @Test
    public void toItem_fromCreateItemDto_shouldMapFields() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        dto.setRequestId(100L);

        Item item = mapper.toItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertNotNull(item.getRequest());
        assertEquals(dto.getRequestId(), item.getRequest().getId());
    }

    @Test
    public void toItem_fromItemDto_shouldMapFieldsAndComments() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Nice");

        ItemDto dto = new ItemDto();
        dto.setId(5L);
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        dto.setRequestId(100L);
        dto.setComments(List.of(commentDto));

        Item item = mapper.toItem(dto);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertEquals(dto.getRequestId(), item.getRequest().getId());
        assertNotNull(item.getComments());
        assertEquals(1, item.getComments().size());
        assertEquals(commentDto.getText(), item.getComments().get(0).getText());
    }

    @Test
    public void updateItemFields_shouldUpdateOnlyNonNullFields() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old desc");
        item.setAvailable(false);

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("New name");
        dto.setAvailable(true);

        Item updated = mapper.updateItemFields(item, dto);

        assertEquals("New name", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.getAvailable());
    }

    @Test
    public void map_shouldReturnItemRequestOrNull() {
        ItemRequest request = mapper.map(100L);
        assertNotNull(request);
        assertEquals(100L, request.getId());

        assertNull(mapper.map(null));
    }

    @Test
    public void toItem_fromItemDto_shouldReturnNullForNull() {
        Item result = mapper.toItem((ItemDto) null);
        assertNull(result);
    }

    @Test
    public void toItem_fromCreateItemDto_shouldReturnNullForNull() {
        Item result = mapper.toItem((CreateItemDto) null);
        assertNull(result);
    }

    @Test
    public void toItemDto_shouldReturnNullForNull() {
        assertNull(mapper.toItemDto(null));
    }
}
