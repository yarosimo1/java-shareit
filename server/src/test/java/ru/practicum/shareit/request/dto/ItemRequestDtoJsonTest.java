package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serializeItemRequestDto() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need item")
                .requestorId(2L)
                .created(LocalDateTime.of(2026, 1, 1, 12, 0))
                .items(List.of())
                .build();

        var content = json.write(dto);

        assertThat(content).hasJsonPathNumberValue("$.id");
        assertThat(content).hasJsonPathStringValue("$.description");
        assertThat(content).hasJsonPathStringValue("$.created");
    }
}
