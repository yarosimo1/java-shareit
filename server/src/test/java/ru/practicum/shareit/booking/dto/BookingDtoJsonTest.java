package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enumStatus.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void serializeBookingDto() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 2, 1, 10, 0))
                .end(LocalDateTime.of(2025, 2, 1, 12, 0))
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-02-01T10:00:00");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-02-01T12:00:00");

        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("APPROVED");
    }
}
