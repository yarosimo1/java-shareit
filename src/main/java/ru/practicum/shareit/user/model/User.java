package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    @NotNull(message = "Id должен быть указан")
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    public User(@NotBlank(message = "Имя не может быть пустым")
                String name,
                @NotBlank(message = "Электронная почта не может быть пустой")
                @Email(message = "Некорректный формат электронной почты")
                String email) {
        this.name = name;
        this.email = email;
    }
}
