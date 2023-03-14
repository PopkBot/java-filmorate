package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Setter
@Builder
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @NotBlank (message = "описание не может быть пустым")
    private String description;
    @NotNull (message = "дата выхода не может быть пустой")
    private LocalDate releaseDate;
    private int duration;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private HashSet<Integer> likedUsersId = new HashSet<>();
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private
    List<Genre> genres = new ArrayList<>();
    @EqualsAndHashCode.Exclude
    private RatingMPA mpa;

}


