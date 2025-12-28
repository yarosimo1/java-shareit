package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
public class Item {
    private Long id;

    @NotNull(message = "Владелец должен быть указан")
    private Long ownerId;

    @NotBlank(message = "Наименование не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private  String description;

    @NotNull(message = "Статус не может быть пустым")
    private Boolean available;

    private ItemRequest request;

    public Item(@NotBlank(message = "Наименование не может быть пустым")
                String name,
                @NotBlank(message = "Описание не может быть пустым")
                String description,
                @NotNull(message = "Статус не может быть пустым")
                Boolean available,
                ItemRequest request) {

        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
