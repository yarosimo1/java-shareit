package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItemDto {
    private String name;
    private String description;
    private Boolean available;
}
