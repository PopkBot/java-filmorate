package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface MpaStorage {

    public RatingMPA getRatingMpaById(int id);

    public List<RatingMPA> getAllRatingMpa();

}
