package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemRequestDto {
    @NotBlank(message = "Описание должно быть указано")
    private String description;
}
