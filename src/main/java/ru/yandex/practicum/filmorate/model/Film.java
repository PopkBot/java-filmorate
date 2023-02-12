package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Film {
    @Setter
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @NotBlank (message = "описание не может быть пустым")
    private String description;
    @NotNull (message = "дата выхода не может быть пустой")
    private LocalDate releaseDate;
    private int duration;
    @Setter
    @EqualsAndHashCode.Exclude
    private HashSet<Integer> likedUsersId = new HashSet<>();

}
