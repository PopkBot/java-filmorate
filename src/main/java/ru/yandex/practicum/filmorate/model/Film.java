package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Setter
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
    private HashSet<Integer> likedUsersId = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private
    List<Genre> genres = new ArrayList<>();
    @EqualsAndHashCode.Exclude
    private RatingMPA mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, HashSet<Integer> likedUsersId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likedUsersId = likedUsersId;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, RatingMPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, HashSet<Integer> likedUsersId,  RatingMPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likedUsersId = likedUsersId;

        this.mpa = mpa;
    }

    public Film() {
    }


}


