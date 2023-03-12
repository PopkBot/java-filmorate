package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private HashSet<Integer> likedUsersId;
    @Setter
    private
    Set<Genre> genreSet=new HashSet<>();
    @Setter
    private RatingMPA ratingMPASet;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, HashSet<Integer> likedUsersId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likedUsersId = likedUsersId;
    }



    public enum Genre{
        Comedy,
        Drama,
        Cartoon,
        Thriller,
        Documentary,
        Action;
    }

    public enum RatingMPA{
        G("G"),
        PG("PG"),
        PG_13("PG-13"),
        R("R"),
        NC_17("NC-17");

        private String ratingName;

        RatingMPA(String s) {
            this.ratingName=s;
        }
        public String getRatingName(){
            return ratingName;
        }
    }
}


