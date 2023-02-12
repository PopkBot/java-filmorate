package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmCount=1;

    @Override
    public HashMap<Integer,Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int id) throws Exception {
        if(!films.containsKey(id)){
            throw new Exception("id = "+id);
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(@Valid Film film) {
        checkFilmValidation(film);
        if(films.containsValue(film)){
            throw new InstanceAlreadyExistException("Не удалось добавить фильм: фильм уже существует");
        }
        film.setId(filmCount);
        films.put(filmCount,film);
        filmCount++;
        log.info("Добавлен фильм {}",film);
        return films.get(filmCount-1);
    }

    @Override
    public Film updateFilm(@Valid Film film) {

        /*if(film.getId()==-1){
            throw new ValidationException("Не удалось обновить фильм: не указан идентификатор");
        }*/
        checkFilmValidation(film);
        if(films.containsKey(film.getId())){
            film.setLikedUsersId(films.get(film.getId()).getLikedUsersId());
            films.replace(film.getId(),film);
            log.info("Обновлен фильм {}",film);
            return film;
        }
        throw new InstanceNotFoundException("Не удалось обновить фильм: фильм не найден.");
    }

    @Override
    public Film deleteFilm(int id) {

        if(!films.containsKey(id)){
            throw new InstanceNotFoundException("Не удалось удалить фильм: фильм не найден.");
        }
        Film removingFilm = films.get(id);
        films.remove(id);
        log.info("Удален фильм {}",removingFilm);
        return removingFilm;
    }

    private void checkFilmValidation(Film film){
        StringBuilder message = new StringBuilder().append("Не удалось добавить фильм: ");
        boolean isValid = true;

        if (film.getDescription().length() > 200) {
            message.append("описание не должно превышать 200 символов; ");
            isValid = false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message.append("фильм не мог быть выпущен до рождения кино; ");
            isValid = false;
        }
        if (film.getDuration()<=0) {
            message.append("длительность фильма должна быть положительной; ");
            isValid = false;
        }
        if (!isValid) {
            throw new ValidationException(message.toString());
        }
    }


}