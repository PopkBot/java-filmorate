package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service()
@Slf4j
public class FilmService {


    private final UserDbStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserDbStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilmById(int filmId) {
        return filmStorage.deleteFilm(filmId);
    }

    public Film getFilmGyId(int id) {
        return filmStorage.getFilmById(id);
    }

    public HashMap<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /**
     * Добавляет идентификатор пользователя в список "нравится" фильма с идентификатором filmId
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void addLike(int filmId, int userId) {

        if(!filmStorage.isPresentInDataBase(filmId)){
            throw new DataNotFoundException("Фильм с id " + filmId + " не найден.");
        }
        if(!userStorage.isPresentInDataBase(userId)){
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден.");
        }

        filmStorage.addLike(filmId,userId);

        log.info("Добавлен лайк к фильму {} от пользователя с id {}", filmId, userId);

    }

    /**
     * Удаляет идентификатор пользователя из списка "нравится" фильма с идентификатором filmId
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void deleteLike(int filmId, int userId) {

        if(!filmStorage.isPresentInDataBase(filmId)){
            throw new DataNotFoundException("Фильм с id " + filmId + " не найден.");
        }
        if(!userStorage.isPresentInDataBase(userId)){
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        filmStorage.deleteLike(filmId,userId);
        log.info("Удален лайк к фильму {} от пользователя с id {}", filmId, userId);
    }

    /**
     * Возвращает список фильмов с наибольшим списком "нравится"
     *
     * @param count размер передаваемого списка фильмов
     * @return List<Film> список самый популярных фильмов
     */
    public List<Film> getMostLikedFilms(int count) {

        if (count <= 0) {
            throw new ValidationException("Число фильмов должно быть положительным");
        }
        ArrayList<Film> mostLikedFilms = new ArrayList<>(filmStorage.getAllFilms().values());
        mostLikedFilms.sort((f1, f2) -> -(f1.getLikedUsersId().size() - f2.getLikedUsersId().size()));
        if (count > mostLikedFilms.size()) {
            count = mostLikedFilms.size();
        }
        log.info("Передан список самых популярных фильмов");
        return mostLikedFilms.subList(0, count);

    }


}
