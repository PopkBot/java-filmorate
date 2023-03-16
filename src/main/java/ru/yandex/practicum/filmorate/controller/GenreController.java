package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreServise;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final GenreServise genreServise;

    @Autowired
    public GenreController(GenreServise genreServise){
        this.genreServise=genreServise;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id){
        log.info("Запрошен жанр с индексом {}",id);
        return genreServise.getGenreById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres(){
        log.info("Запрошены все жанры");
        return genreServise.getAllGenres();
    }
}
