package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping
@Slf4j
public class FilmController {



    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return null;
    }


    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {



        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){

            return film;

    }





}



