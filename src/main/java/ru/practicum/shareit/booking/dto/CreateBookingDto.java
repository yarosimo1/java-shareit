package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
public class CreateBookingDto {
    @DateTimeFormat
    @NotNull
    private LocalDateTime start;

    @DateTimeFormat
    @NotNull
    private LocalDateTime  end;

    @NotNull
    private Long itemId;
}
