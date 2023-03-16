package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.*;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;

@Component()
@Slf4j
public class FilmDao implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Возвращает таблицу всех фильмов
     *
     * @return - HashMap<Integer,User>
     */
    @Override
    public HashMap<Integer, Film> getAllFilms() {

        HashMap<Integer, Film> films = new HashMap<>();
        SqlRowSet idsRows = jdbcTemplate.queryForRowSet(
                "SELECT " + FilmTableConstants.FILM_ID + " FROM " + FilmTableConstants.TABLE_NAME);
        while (idsRows.next()) {
            int id = idsRows.getInt(FilmTableConstants.FILM_ID);
            films.put(id, loadFilmFromDbById(id));
        }
        log.info("Передан список всех фильмов");
        return films;
    }

    /**
     * Возвращает фильм по идентификатору
     *
     * @param id идентификатор фильм, которого необходимо передать
     * @return Film пользователь с запрошенным id
     */
    @Override
    public Film getFilmById(int id) {

        Film film = loadFilmFromDbById(id);
        log.info("Передан фильм id = {}", id);
        return film;
    }

    private Film loadFilmFromDbById(int id) {

        if (!isPresentInDataBase(id)) {
            throw new DataNotFoundException("Фильм с id " + id + " не найден.");
        }
        Film film = new Film();
        extractFilmCoreFromDB(id, film);
        extractFilmGenresFromDB(id, film);
        extractFilmLikesFromDB(id, film);
        extractFilmMpaFromDB(id, film);
        checkFilmValidation(film);
        System.out.println(film);
        return film;
    }

    private void extractFilmCoreFromDB(int id, Film film) {

        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM " + FilmTableConstants.TABLE_NAME + " AS f\n"
                        + "INNER JOIN " + RatingMpaTableConstants.TABLE_NAME + " AS mpa "
                        + "ON mpa." + RatingMpaTableConstants.RATING_MPA_ID + " = f." + FilmTableConstants.RATING_MPA_ID
                        + "\nWHERE " + FilmTableConstants.FILM_ID + " = ?", id);
        if (filmRow.next()) {
            film.setId(id);
            film.setName(filmRow.getString(FilmTableConstants.NAME));
            film.setDescription(filmRow.getString(FilmTableConstants.DESCRIPTION));
            film.setReleaseDate(Date.valueOf(filmRow.getString(FilmTableConstants.RELEASE_DATE)).toLocalDate());
            film.setDuration(filmRow.getInt(FilmTableConstants.DURATION));
        }
    }

    private void extractFilmGenresFromDB(int id, Film film) {
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet(
                "SELECT ge." + GenreTableConstants.GENRE_ID + " ,ge." + GenreTableConstants.GENRE_NAME
                        + "\nFROM " + FilmsToGenresTableConstants.TABLE_NAME + " AS ftg\n"
                        + "INNER JOIN " + GenreTableConstants.TABLE_NAME + " AS ge ON "
                        + "ge." + GenreTableConstants.GENRE_ID + "= ftg." + FilmsToGenresTableConstants.GENRE_ID
                        + "\nWHERE ftg." + FilmsToGenresTableConstants.FILM_ID + "= ?", id
        );

        while (genresRow.next()) {
            film.getGenres().add(new Genre(genresRow.getInt(GenreTableConstants.GENRE_ID)
                    , genresRow.getString(GenreTableConstants.GENRE_NAME)));
        }
        Collections.sort(film.getGenres(),(g1,g2)->g1.getId()-g2.getId());
    }

    private void extractFilmLikesFromDB(int id, Film film) {
        SqlRowSet likedRow = jdbcTemplate.queryForRowSet(
                "SELECT " + LikedFilmsTableConstants.USER_ID
                        + "\nFROM " + LikedFilmsTableConstants.TABLE_NAME
                        + "\nWHERE " + LikedFilmsTableConstants.FILM_ID + " = ?", id
        );
        while (likedRow.next()) {
            film.getLikedUsersId().add(likedRow.getInt(LikedFilmsTableConstants.USER_ID));
        }
    }

    private void extractFilmMpaFromDB(int id, Film film) {
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(
                "SELECT f." + FilmTableConstants.RATING_MPA_ID + ", mpa." + RatingMpaTableConstants.RATING_MPA_NAME
                        + "\nFROM " + FilmTableConstants.TABLE_NAME + " AS f\n"
                        + "INNER JOIN " + RatingMpaTableConstants.TABLE_NAME + " AS mpa "
                        + "ON mpa." + RatingMpaTableConstants.RATING_MPA_ID + " = f." + FilmTableConstants.RATING_MPA_ID
                        + "\nWHERE " + FilmTableConstants.FILM_ID + "= ?", id
        );
        if (mpaRow.next()) {
            film.setMpa(new RatingMPA(mpaRow.getInt(FilmTableConstants.RATING_MPA_ID),
                    mpaRow.getString(RatingMpaTableConstants.RATING_MPA_NAME)));
        }

    }

    public boolean isPresentInDataBase(Film film) {

        SqlRowSet filmsRows = getIdRowsFromDb(film);
        if (filmsRows.next()) {
            return true;
        }
        return false;
    }

    private SqlRowSet getIdRowsFromDb(Film film) {

        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(
                "SELECT " + FilmTableConstants.FILM_ID + "\n"
                        + "FROM " + FilmTableConstants.TABLE_NAME + "\n"
                        + "WHERE " + FilmTableConstants.NAME + "=? AND "
                        + FilmTableConstants.DESCRIPTION + "=? AND "
                        + FilmTableConstants.DURATION + "=? AND "
                        + FilmTableConstants.RELEASE_DATE + "=?;"


                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , Date.valueOf(film.getReleaseDate()));
        return filmsRows;
    }

    public boolean isPresentInDataBase(int id) {

        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(
                "SELECT " + FilmTableConstants.FILM_ID + "\n"
                        + "FROM " + FilmTableConstants.TABLE_NAME + "\n"
                        + "WHERE " + FilmTableConstants.FILM_ID + "=?;", id);
        if (filmsRows.next()) {
            return true;
        }
        return false;
    }

    /**
     * Добавляет фильм в таблицу
     *
     * @param film Film добавляемый фильм
     * @return - Film в случае успешного добавления фильм возвращает добавленный объект
     */
    @Override
    public Film addFilm(Film film) {

        checkFilmValidation(film);
        if (isPresentInDataBase(film)) {
            throw new InstanceAlreadyExistException("Не удалось добавить фильм: фильм уже существует");
        }
        try {
            jdbcTemplate.update(
                    "INSERT INTO " + FilmTableConstants.TABLE_NAME
                            + " (" + FilmTableConstants.NAME
                            + "," + FilmTableConstants.DESCRIPTION
                            + "," + FilmTableConstants.RELEASE_DATE
                            + "," + FilmTableConstants.DURATION
                            + "," + FilmTableConstants.RATING_MPA_ID + ")"
                            + " VALUES(?,?,?,?,?)",
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId()
            );
            SqlRowSet filmRows = getIdRowsFromDb(film);
            if (filmRows.next()) {
                film.setId(filmRows.getInt(FilmTableConstants.FILM_ID));
            }

            for (Genre genre : film.getGenres()) {

                SqlRowSet genresRows = jdbcTemplate.queryForRowSet(
                        "SELECT * FROM " + FilmsToGenresTableConstants.TABLE_NAME
                                + " WHERE " + FilmsToGenresTableConstants.GENRE_ID + " = ? AND "
                                + FilmsToGenresTableConstants.FILM_ID + "= ?",
                        genre.getId(), film.getId()
                );
                if (!genresRows.next()) {

                    jdbcTemplate.update(
                            "INSERT INTO " + FilmsToGenresTableConstants.TABLE_NAME
                                    + "(" + FilmsToGenresTableConstants.GENRE_ID
                                    + "," + FilmsToGenresTableConstants.FILM_ID
                                    + ") VALUES (?,?);",
                            genre.getId(), film.getId()
                    );
                }
            }

            film = loadFilmFromDbById(film.getId());

            log.info("Добавлен фильм {}", film);
        }catch (DataIntegrityViolationException | BadSqlGrammarException ex){
            SqlRowSet filmRows = getIdRowsFromDb(film);
            if (filmRows.next()) {
                film.setId(filmRows.getInt(FilmTableConstants.FILM_ID));
                deleteFilm(film.getId());
            }
            throw new RuntimeException("SQL exception");
        }

        return film;
    }

    /**
     * Обновляет фильм в таблице
     *
     * @param film обновленная версия фильм, содержит идентификатор Id
     * @return - Film в случае успешного обновления фильм возвращает добавленный объект
     */
    @Override
    public Film updateFilm(Film film) {
        Film buffFilm = new Film();
        try {
            if (isPresentInDataBase(film.getId())) {
                buffFilm = loadFilmFromDbById(film.getId());
                jdbcTemplate.update(
                        "UPDATE " + FilmTableConstants.TABLE_NAME
                                + " SET "
                                + FilmTableConstants.NAME + "= ?,"
                                + FilmTableConstants.DESCRIPTION + "= ?,"
                                + FilmTableConstants.RELEASE_DATE + "= ?,"
                                + FilmTableConstants.DURATION + "= ?,"
                                + FilmTableConstants.RATING_MPA_ID + "= ?"
                                + "\nWHERE " + FilmTableConstants.FILM_ID + "= ? ;"
                        ,
                        film.getName(),
                        film.getDescription(),
                        Date.valueOf(film.getReleaseDate()),
                        film.getDuration(),
                        film.getMpa().getId(),
                        film.getId());

                jdbcTemplate.execute(
                        "DELETE FROM " + FilmsToGenresTableConstants.TABLE_NAME
                                + " WHERE " + FilmsToGenresTableConstants.FILM_ID + "= " + film.getId()
                );

                for (Genre genre : film.getGenres()) {

                    SqlRowSet genresRows = jdbcTemplate.queryForRowSet(
                            "SELECT * FROM " + FilmsToGenresTableConstants.TABLE_NAME
                                    + " WHERE " + FilmsToGenresTableConstants.GENRE_ID + " = ? AND "
                                    + FilmsToGenresTableConstants.FILM_ID + "= ?",
                            genre.getId(), film.getId()
                    );
                    if (!genresRows.next()) {

                        jdbcTemplate.update(
                                "INSERT INTO " + FilmsToGenresTableConstants.TABLE_NAME
                                        + "(" + FilmsToGenresTableConstants.GENRE_ID
                                        + "," + FilmsToGenresTableConstants.FILM_ID
                                        + ") VALUES (?,?);",
                                genre.getId(), film.getId()
                        );
                    }
                }

                film = loadFilmFromDbById(film.getId());
                log.info("Обновлен фильм {}", film);
                return film;

            }
            throw new DataNotFoundException("Не удалось обновить фильм: фильм не найден.");
        }catch (DataIntegrityViolationException | BadSqlGrammarException ex){
            updateFilm(buffFilm);
            throw new RuntimeException("SQL exception");
        }
    }

    /**
     * Удаляет фильм с идентификатором id из таблицы
     *
     * @param id идентификатор фильм, которого необходимо удалить
     * @return - User копия удаленного фильм возвращается в случае успешного удаления из таблицы
     */
    @Override
    public Film deleteFilm(int id) {

        if (!isPresentInDataBase(id)) {
            throw new DataNotFoundException("Не удалось удалить фильм: фильм не найден.");
        }
        Film removingFilm = loadFilmFromDbById(id);
        jdbcTemplate.execute(
                "DELETE FROM " + FilmTableConstants.TABLE_NAME
                        + " WHERE " + FilmTableConstants.FILM_ID + "= " + id);
        log.info("Удален пользователь {}", removingFilm);

        if(getAllFilms().size()==0){
            jdbcTemplate.execute(
                    "ALTER TABLE "+FilmTableConstants.TABLE_NAME
                    +" ALTER COLUMN "+FilmTableConstants.FILM_ID+" RESTART WITH 1");
        }

        return removingFilm;
    }

    /**
     * Удаляет все фильмы из таблицы, восстанавливает
     */
    @Override
    public void deleteAllFilms() {

        SqlRowSet idsRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + FilmTableConstants.TABLE_NAME);
        while (idsRows.next()) {
            jdbcTemplate.execute(
                    "DELETE FROM " + FilmTableConstants.TABLE_NAME
                            + " WHERE " + FilmTableConstants.FILM_ID + " = " + idsRows.getInt(FilmTableConstants.FILM_ID));
        }
        jdbcTemplate.execute(
                "ALTER TABLE "+FilmTableConstants.TABLE_NAME
                        +" ALTER COLUMN "+FilmTableConstants.FILM_ID+" RESTART WITH 1");

        log.info("Таблица фильмов очищена");
    }

    @Override
    public void addLike(int filmId, int userId) {

        SqlRowSet likeRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM " + LikedFilmsTableConstants.TABLE_NAME
                        + " WHERE " + LikedFilmsTableConstants.FILM_ID + " = " + filmId
                        + " AND " + LikedFilmsTableConstants.USER_ID + " = " + userId
        );
        if (likeRow.next()) {
            throw new InstanceAlreadyExistException("Лайк уже стоит");
        }

        jdbcTemplate.update(
                "INSERT INTO " + LikedFilmsTableConstants.TABLE_NAME
                        + " (" + LikedFilmsTableConstants.USER_ID
                        + "," + LikedFilmsTableConstants.FILM_ID + ")\n"
                        + "VALUES (?,?);", userId, filmId
        );

    }

    @Override
    public void deleteLike(int filmId, int userId) {

        jdbcTemplate.execute(
                "DELETE FROM " + LikedFilmsTableConstants.TABLE_NAME
                        + "\nWHERE " + LikedFilmsTableConstants.FILM_ID + " = " + filmId
                        + " AND " + LikedFilmsTableConstants.USER_ID + " = " + userId
        );
    }

    /**
     * Проверяет поля фильма на корректность
     *
     * @param film фильм, поля которого необходимо проверить
     */
    private void checkFilmValidation(Film film) {
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
        if (film.getDuration() <= 0) {
            message.append("длительность фильма должна быть положительной; ");
            isValid = false;
        }
        if (!isValid) {
            throw new ValidationException(message.toString());
        }
    }


}
