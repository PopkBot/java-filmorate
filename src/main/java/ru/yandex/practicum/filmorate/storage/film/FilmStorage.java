package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface FilmStorage {

    public HashMap<Integer,Film> getAllFilms();

    public Film getFilmById(int id);

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film deleteFilm(int id);

    public void deleteAllFilms();

    public void addLike(int filmId,int userId);

    public void deleteLike(int filmId,int userId);

    public boolean isPresentInDataBase(Film film);

    public boolean isPresentInDataBase(int filmId);

}
