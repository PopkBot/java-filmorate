package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmCount=1;


    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<Film>(films.values());
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        checkFilmValidation(film);
        if(films.containsKey(film.getId())){
            throw new InstanceAlreadyExistException("Не удалось добавить фильм: фильм уже существует");
        }
        films.put(filmCount,film);
        film.setId(filmCount);
        filmCount++;
        log.info("Добавлен фильм {}",film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film){
        checkFilmValidation(film);
        if(films.containsKey(film.getId())){
            films.replace(film.getId(),film);
            log.info("Обновлен фильм {}",film);
            return film;
        }
        throw new InstanceNotFoundException("Не удалось обновить фильм: фильм не найден.");

    }

    @DeleteMapping
    public void deleteFilms(){
        films.clear();
        filmCount=1;
        log.info("Удаление всех записей фильмов");
    }

    private void checkFilmValidation(Film film){
        StringBuilder message = new StringBuilder().append("Не удалось добавить фильм: ");
        boolean isValid = true;
        if (film.getName().isBlank()) {
            message.append("название фильма не может быть пустым; ");
            isValid = false;
        }
        if (film.getDescription().length() > 200) {
            message.append("описание не должно превышать 200 символов; ");
            isValid = false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message.append("фильм не мог быть выпущен до рождения кино; ");
            isValid = false;
        }
        if (film.getDuration()<0) {
            message.append("длительность фильма должна быть положительной; ");
            isValid = false;
        }
        if (!isValid) {
            throw new ValidationException(message.toString());
        }
    }

}



