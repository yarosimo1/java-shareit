package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serializeCommentDto() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Great item!");
        dto.setAuthorName("John");
        dto.setCreated(LocalDateTime.of(2025, 3, 1, 12, 30));

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Great item!");

        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("John");

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-03-01T12:30:00");
    }
}
