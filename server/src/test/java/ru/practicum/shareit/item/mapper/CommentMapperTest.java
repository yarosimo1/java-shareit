package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {

    private final CommentMapper mapper = CommentMapper.INSTANCE;

    @Test
    public void toCommentDto_shouldMapAuthorNameAndFields() {
        User author = new User(1L, "Alice", "alice@mail.com");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Great item!");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2026, 2, 11, 15, 0));

        CommentDto dto = mapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    public void toCommentDto_shouldReturnNull_whenCommentIsNull() {
        CommentDto dto = mapper.toCommentDto(null);

        assertNull(dto);
    }

    @Test
    public void toCommentDto_shouldSetAuthorNameNull_whenAuthorIsNull() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("No author");
        comment.setAuthor(null); // ← ВАЖНО
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = mapper.toCommentDto(comment);

        assertNull(dto.getAuthorName());
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

}
