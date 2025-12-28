package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class CreateItemDto {
    @NotBlank(message = "Наименование не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private  String description;

    @NotNull(message = "Статус не может быть пустым")
    private Boolean available;

    private ItemRequest request;
}
