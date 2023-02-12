package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class FilmService {


    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage,FilmStorage filmStorage){
        this.userStorage=userStorage;
        this.filmStorage=filmStorage;
    }

    public Film addFilm(Film film){
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film){
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilmById(int filmId){
        return filmStorage.deleteFilm(filmId);
    }

    public Film getFilmGyId(int id){
        try {
            return filmStorage.getFilmById(id);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Фильм с "+e.getMessage()+" не найден.");
        }
    }

    public HashMap<Integer,Film> getAllFilms(){
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId){

        Film film;
        try {
            film=filmStorage.getFilmById(filmId);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось поставить лайк: фильм не найден");
        }
        try {
            userStorage.getUserById(userId);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось поставить лайк: пользователь не найден");
        }
        film.getLikedUsersId().add(userId);

    }

    public void deleteLike(int filmId, int userId){

        Film film;
        try {
            film=filmStorage.getFilmById(filmId);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось удалить лайк: фильм не найден");
        }
        try {
            userStorage.getUserById(userId);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось удалить лайк: пользователь не найден");
        }
        film.getLikedUsersId().remove(userId);
    }

    public List<Film> getMostLikedFilms(int count){

        if(count<=0){
            throw new ValidationException("Число фильмов должно быть положительным");
        }

        ArrayList<Film> mostLikedFilms = new ArrayList<>(filmStorage.getAllFilms().values());
        mostLikedFilms.sort((f1,f2)-> f1.getLikedUsersId().size()-f2.getLikedUsersId().size());
        if(count>mostLikedFilms.size()){
            count=mostLikedFilms.size();
        }
        return mostLikedFilms.subList(0,count);


    }


}
