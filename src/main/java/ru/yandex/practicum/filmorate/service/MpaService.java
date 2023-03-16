package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service()
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public RatingMPA getRatingMPAById(int id){
        RatingMPA ratingMPA = mpaStorage.getRatingMpaById(id);
        log.info("Передан MPA рейтинг {}",ratingMPA);
        return ratingMPA;
    }

    public List<RatingMPA> getAllRatingMpa(){
        List<RatingMPA> ratingMPA = mpaStorage.getAllRatingMpa();
        log.info("Передан список всех MPA рейтингов ");
        return ratingMPA;
    }


}
