package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService){
        this.mpaService=mpaService;
    }

    @GetMapping("/mpa/{id}")
    public RatingMPA getMPA(@PathVariable int id){
        log.info("Запрошен MPA рейтинг с индексом {}",id);
        return mpaService.getRatingMPAById(id);
    }

    @GetMapping("/mpa")
    public List<RatingMPA> getMPA(){
        log.info("Запрошен список всех MPA рейтингов");
        return mpaService.getAllRatingMpa();
    }

}
