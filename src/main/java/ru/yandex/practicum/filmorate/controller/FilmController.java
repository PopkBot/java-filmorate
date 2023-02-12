package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService){
        this.filmService=filmService;
    }

    @DeleteMapping("/films")
    public void deleteAllFilms(){
        filmService.deleteAllFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film){
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms().values();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id){
        return filmService.getFilmGyId(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id , @PathVariable int userId){
        filmService.addLike(id,userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id , @PathVariable int userId){
        filmService.deleteLike(id,userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false) Integer count){
        if(count==null){
            count= Constants.MOST_LIKED_FILMS_NUMBER;
        }
        return filmService.getMostLikedFilms(count);
    }

}



