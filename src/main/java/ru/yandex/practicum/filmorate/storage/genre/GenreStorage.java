package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface GenreStorage {

    public Genre getGenreById(int id);

    public List<Genre> getAllGenres();
}
