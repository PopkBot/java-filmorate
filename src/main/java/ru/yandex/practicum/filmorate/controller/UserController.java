package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;



import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userCount=1;

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @DeleteMapping("/users")
    public void deleteFilms(){
        users.clear();
        userCount=1;
        log.info("Удаление всех записей пользователей");
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {

        checkUserValidation(user);
        if(users.containsKey(user.getId())){
            throw new InstanceAlreadyExistException("Не удалось добавить пользователя: пользователь уже существует");
        }
        users.put(userCount,user);
        user.setId(userCount);
        userCount++;
        log.info("Добавлен пользователь {}",user);
        return user;
    }


    /*С валидацией и путем @PutMapping("/updateUser") то же самое. Нужно чем-то одним валидировать
    * "Одним валидатором" вы имели в виду, чтобы поле проверялось либо только @NotNull\@NotBlank
    *  либо только if (user.getName().isBlank()) и не совмещать эти две валидации?  */

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){
        checkUserValidation(user);
        if(users.containsKey(user.getId())){
            users.replace(user.getId(),user);
            log.info("Обновлен пользователь {}",user);
            return user;
        }
        throw new InstanceNotFoundException("Не удалось обновить пользователя: пользователь не найден.");
    }

    private void checkUserValidation( User user) {
        StringBuilder message = new StringBuilder().append("Не удалось добавить пользователя: ");
        boolean isValid = true;
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            message.append("неверный формат логина; ");
            isValid = false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            message.append("пользователь из будущего; ");
            isValid = false;
        }
        if (!isValid) {
            throw new ValidationException(message.toString());
        }
    }
}
