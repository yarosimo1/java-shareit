package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    @NotNull(message = "Id должен быть указан")
    private Long id;

    @DateTimeFormat
    @NotNull
    private Instant start;

    @DateTimeFormat
    @NotNull
    private Instant end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private Status status;
}
