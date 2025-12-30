package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    @NotNull(message = "Id должен быть указан")
    private Long id;

    @NotBlank(message = "Описание должно быть указано")
    private String description;

    @NotNull(message = "Пользователь должен быть указан")
    private User requestor;

    @NotNull
    @DateTimeFormat
    private Instant created;
}
