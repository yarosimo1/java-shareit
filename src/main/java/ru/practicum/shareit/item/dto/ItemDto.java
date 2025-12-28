package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
    private ItemRequest request;
}
