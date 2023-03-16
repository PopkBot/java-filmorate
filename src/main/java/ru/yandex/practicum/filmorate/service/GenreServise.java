package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service()
@Slf4j
public class GenreServise {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreServise(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int id){
        Genre genre = genreStorage.getGenreById(id);
        log.info("Передан жанр {}",genre);
        return genre;
    }

    public List<Genre> getAllGenres(){
        List<Genre> genres = genreStorage.getAllGenres();
        log.info("Переданы все жанры");
        return genres;
    }
}
