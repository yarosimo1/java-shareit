package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeDeserializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item 1");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(LocalDateTime.of(2026, 2, 10, 12, 0));
        itemDto.setNextBooking(LocalDateTime.of(2026, 2, 12, 12, 0));
        itemDto.setComments(Collections.emptyList());

        // Сериализация
        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("Item 1", "Description");

        // Десериализация
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);
        assertThat(deserialized.getId()).isEqualTo(itemDto.getId());
        assertThat(deserialized.getLastBooking()).isEqualTo(itemDto.getLastBooking());
        assertThat(deserialized.getNextBooking()).isEqualTo(itemDto.getNextBooking());
    }
}
