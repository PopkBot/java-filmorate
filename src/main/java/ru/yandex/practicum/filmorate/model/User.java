package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class User {

    @Setter
    @EqualsAndHashCode.Exclude
    private int id;
    @Email(message = "неверный формат email")
    @NotBlank (message = "email не может быть пустым")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    private String login;
    @Setter
    private String name;
    @NotNull (message = "дата рождения не может быть пустой")
    private LocalDate birthday;



}
