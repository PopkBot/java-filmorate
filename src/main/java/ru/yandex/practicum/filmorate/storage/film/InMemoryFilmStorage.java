package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmCount=1;

    /**
     * Возвращает таблицу всех фильмов
     * @return - HashMap<Integer,User>
     */
    @Override
    public HashMap<Integer,Film> getAllFilms() {
        log.info("Запрошен список всех фильмов");
        return films;
    }

    /**
     * Возвращает фильм по идентификатору
     * @param id идентификатор фильм, которого необходимо передать
     * @return Film пользователь с запрошенным id
     * @throws Exception - фильм с указанным id не найден в таблицу
     */
    @Override
    public Film getFilmById(int id) throws Exception {
        if(!films.containsKey(id)){
            throw new Exception("id = "+id);
        }
        log.info("Запрошен фильм id = {}",id);
        return films.get(id);
    }

    /**
     * Добавляет фильм в таблицу
     * @param film Film добавляемый фильм
     * @return - Film в случае успешного добавления фильм возвращает добавленный объект
     */
    @Override
    public Film addFilm(Film film) {
        checkFilmValidation(film);
        if(films.containsValue(film)){
            throw new InstanceAlreadyExistException("Не удалось добавить фильм: фильм уже существует");
        }
        film.setId(filmCount);
        film.setLikedUsersId(new HashSet<>());
        films.put(filmCount,film);
        filmCount++;
        log.info("Добавлен фильм {}",film);
        return films.get(filmCount-1);
    }

    /**
     * Обновляет фильм в таблице
     * @param film обновленная версия фильм, содержит идентификатор Id
     * @return - Film в случае успешного обновления фильм возвращает добавленный объект
     */
    @Override
    public Film updateFilm(Film film) {

        checkFilmValidation(film);
        if(films.containsKey(film.getId())){
            film.setLikedUsersId(films.get(film.getId()).getLikedUsersId());
            films.replace(film.getId(),film);
            log.info("Обновлен фильм {}",film);
            return film;
        }
        throw new InstanceNotFoundException("Не удалось обновить фильм: фильм не найден.");
    }

    /**
     * Удаляет фильм с идентификатором id из таблицы
     * @param id идентификатор фильм, которого необходимо удалить
     * @return - User копия удаленного фильм возвращается в случае успешного удаления из таблицы
     */
    @Override
    public Film deleteFilm(int id) {

        if(!films.containsKey(id)){
            throw new InstanceNotFoundException("Не удалось удалить фильм: фильм не найден.");
        }
        Film removingFilm = films.get(id);
        films.remove(id);
        log.info("Удален фильм {}",removingFilm);
        return removingFilm;
    }

    /**
     * Удаляет все фильмы из таблицы, восстанавливает счетчик идентификаторов
     */
    @Override
    public void deleteAllFilms() {
        filmCount=1;
        films.clear();
        log.info("Список фильмов очищен");
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
