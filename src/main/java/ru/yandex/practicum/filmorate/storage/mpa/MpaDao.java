package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.RatingMpaTableConstants;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.ArrayList;
import java.util.List;
@Component()
public class MpaDao implements MpaStorage{

    private final JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RatingMPA getRatingMpaById(int id) {

        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM "+ RatingMpaTableConstants.TABLE_NAME
                +" WHERE "+RatingMpaTableConstants.RATING_MPA_ID+" = ?",id
        );
        RatingMPA ratingMPA = new RatingMPA();
        if(mpaRow.next()){
            ratingMPA = new RatingMPA(id,mpaRow.getString(RatingMpaTableConstants.RATING_MPA_NAME));
            return ratingMPA;
        }
        throw new DataNotFoundException("Рейтинг с индексом "+id+" не найден");

    }

    @Override
    public List<RatingMPA> getAllRatingMpa() {
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet("SELECT * FROM "+ RatingMpaTableConstants.TABLE_NAME);
        List<RatingMPA> ratingMPA = new ArrayList<>();
        while (mpaRow.next()){
            int id = mpaRow.getInt(RatingMpaTableConstants.RATING_MPA_ID);
            String name = mpaRow.getString(RatingMpaTableConstants.RATING_MPA_NAME);
            ratingMPA.add(new RatingMPA(id,name));
        }
        return ratingMPA;
    }

}
