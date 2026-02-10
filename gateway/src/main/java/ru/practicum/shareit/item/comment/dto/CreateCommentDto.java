package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDto {
    @NotNull
    private String text;
}