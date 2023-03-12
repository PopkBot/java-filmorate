package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.*;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    public InMemoryFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Возвращает таблицу всех фильмов
     * @return - HashMap<Integer,User>
     */
    @Override
    public HashMap<Integer,Film> getAllFilms() {

        HashMap<Integer,Film> films = new HashMap<>();
        SqlRowSet idsRows = jdbcTemplate.queryForRowSet(
                "SELECT "+FilmTableConstants.FILM_ID+" FROM "+FilmTableConstants.TABLE_NAME);
        while (idsRows.next()){
            int id = idsRows.getInt(FilmTableConstants.FILM_ID);
            films.put(id,loadFilmFromDbById(id));
        }
        log.info("Передан список всех фильмов");
        return films;
    }

    /**
     * Возвращает фильм по идентификатору
     * @param id идентификатор фильм, которого необходимо передать
     * @return Film пользователь с запрошенным id
     */
    @Override
    public Film getFilmById(int id) {

        Film film = loadFilmFromDbById(id);
        log.info("Передан фильм id = {}",id);
        return film;
    }

    private Film loadFilmFromDbById(int id){

         if(!isPresentInDataBase(id)){
            throw new DataNotFoundException("Фильм с id "+id+" не найден.");
        }
        Film film = new Film();
        extractFilmCoreFromDB(id,film);
        extractFilmGenresFromDB(id,film);
        extractFilmLikesFromDB(id,film);
        checkFilmValidation(film);
        return film;
    }

    private void extractFilmCoreFromDB(int id,Film film){

        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM "+FilmTableConstants.TABLE_NAME+" AS f\n"
                        +"INNER JOIN "+RatingMpaTableConstants.TABLE_NAME+" AS mpa "
                        +"ON mpa."+RatingMpaTableConstants.RATING_MPA_ID+" = f."+FilmTableConstants.RATING_MPA_ID
                        +"\nWHERE "+FilmTableConstants.FILM_ID+" = ?",id);
        if(filmRow.next()) {
            film = new Film(
                    id,
                    filmRow.getString(FilmTableConstants.NAME),
                    filmRow.getString(FilmTableConstants.DESCRIPTION),
                    Date.valueOf(filmRow.getString(FilmTableConstants.NAME)).toLocalDate(),
                    filmRow.getInt(FilmTableConstants.DURATION),
                    Film.RatingMPA.valueOf(filmRow.getString(RatingMpaTableConstants.RATING_MPA_NAME))
            );
        }
    }

    private void extractFilmGenresFromDB(int id,Film film){
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet(
                "SELECT g."+ GenreTableConstants.GENRE_NAME
                        +"\nFROM "+ FilmsToGenresTableConstanst.TABLE_NAME+" AS ftg\n"
                        +"INNER JOIN "+GenreTableConstants.TABLE_NAME+" AS g ON "
                        +"g."+GenreTableConstants.GENRE_ID+"= ftg."+FilmsToGenresTableConstanst.GENRE_ID
                        +"\nWHERE ftg."+FilmsToGenresTableConstanst.FILM_ID+"= ?",id
        );

        while (genresRow.next()){
            film.getGenreSet().add(Film.Genre.valueOf(genresRow.getString(GenreTableConstants.GENRE_NAME)));
        }
    }

    private void extractFilmLikesFromDB(int id,Film film){
        SqlRowSet likedRow = jdbcTemplate.queryForRowSet(
                "SELECT "+ LikedFilmsTableConstants.USER_ID
                        +"\nFROM "+LikedFilmsTableConstants.TABLE_NAME
                        +"\nWHERE "+LikedFilmsTableConstants.FILM_ID+" = ?",id
        );
        while (likedRow.next()){
            film.getLikedUsersId().add(likedRow.getInt(LikedFilmsTableConstants.USER_ID));
        }
    }

    private boolean isPresentInDataBase(Film film){

        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(
                "SELECT " + FilmTableConstants.FILM_ID + "\n"
                        + "FROM " + FilmTableConstants.TABLE_NAME + "\n"
                        + "WHERE " + FilmTableConstants.NAME + "=? AND "
                        + FilmTableConstants.DESCRIPTION + "=? AND "
                        + FilmTableConstants.DURATION + "=? AND "
                        + FilmTableConstants.RELEASE_DATE + "=? AND "
                        + FilmTableConstants.RATING_MPA_ID + "=?;"

                , film.getName()
                ,film.getDescription()
                ,film.getDuration()
                , Date.valueOf(film.getReleaseDate())
                ,film.getRatingMPA().name());
        if (filmsRows.next()) {
            return true;
        }
        return false;
    }

    private boolean isPresentInDataBase(int id){

        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(
                "SELECT " + FilmTableConstants.FILM_ID + "\n"
                        + "FROM " + FilmTableConstants.TABLE_NAME + "\n"
                        + "WHERE " + FilmTableConstants.NAME + "=?;",id);
        if (filmsRows.next()) {
            return true;
        }
        return false;
    }

    /**
     * Добавляет фильм в таблицу
     * @param film Film добавляемый фильм
     * @return - Film в случае успешного добавления фильм возвращает добавленный объект
     */
    @Override
    public Film addFilm(Film film) {
      /*  checkFilmValidation(film);
        if(films.containsValue(film)){
            throw new InstanceAlreadyExistException("Не удалось добавить фильм: фильм уже существует");
        }
        film.setId(filmCount);
        film.setLikedUsersId(new HashSet<>());
        films.put(filmCount,film);
        filmCount++;
        log.info("Добавлен фильм {}",film);
        return films.get(filmCount-1);*/
        return null;
    }

    /**
     * Обновляет фильм в таблице
     * @param film обновленная версия фильм, содержит идентификатор Id
     * @return - Film в случае успешного обновления фильм возвращает добавленный объект
     */
    @Override
    public Film updateFilm(Film film) {

        /*checkFilmValidation(film);
        if(films.containsKey(film.getId())){
            film.setLikedUsersId(films.get(film.getId()).getLikedUsersId());
            films.replace(film.getId(),film);
            log.info("Обновлен фильм {}",film);
            return film;
        }
        throw new DataNotFoundException("Не удалось обновить фильм: фильм не найден.");*/
        return null;
    }

    /**
     * Удаляет фильм с идентификатором id из таблицы
     * @param id идентификатор фильм, которого необходимо удалить
     * @return - User копия удаленного фильм возвращается в случае успешного удаления из таблицы
     */
    @Override
    public Film deleteFilm(int id) {

        /*if(!films.containsKey(id)){
            throw new DataNotFoundException("Не удалось удалить фильм: фильм не найден.");
        }
        Film removingFilm = films.get(id);
        films.remove(id);
        log.info("Удален фильм {}",removingFilm);
        return removingFilm;*/
        return null;
    }

    /**
     * Удаляет все фильмы из таблицы, восстанавливает счетчик идентификаторов
     */
    @Override
    public void deleteAllFilms() {
        /*filmCount=1;
        films.clear();
        log.info("Список фильмов очищен");*/
    }

    /**
     * Проверяет поля фильма на корректность
     * @param film фильм, поля которого необходимо проверить
     */
    private void checkFilmValidation(Film film){
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
        if (film.getDuration()<=0) {
            message.append("длительность фильма должна быть положительной; ");
            isValid = false;
        }
        if (!isValid) {
            throw new ValidationException(message.toString());
        }
    }


}
