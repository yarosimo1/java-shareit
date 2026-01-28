package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentDto {
    @NotNull
    private String text;
}
