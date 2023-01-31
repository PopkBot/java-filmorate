package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Film {
    @Setter
    private int id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @NotNull (message = "описание не может быть пустым")
    private String description;
    @NotNull (message = "дата выхода не может быть пустой")
    private LocalDate releaseDate;
    private int duration;
}
