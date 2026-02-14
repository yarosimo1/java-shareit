package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreateBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    public void serialize() throws Exception {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2025, 1, 10, 12, 0))
                .end(LocalDateTime.of(2025, 1, 11, 12, 0))
                .build();

        JsonContent<CreateBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-01-10T12:00:00");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-01-11T12:00:00");
    }
}