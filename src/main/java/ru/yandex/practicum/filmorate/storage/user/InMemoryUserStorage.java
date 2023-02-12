package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userCount=1;

    @Override
    public HashMap<Integer,User> getAllUsers(){
        return users;
    }

    @Override
    public User addUser(@Valid User user) {
        checkUserValidation(user);
        if(users.containsValue(user)){
            throw new InstanceAlreadyExistException("Не удалось добавить пользователя: пользователь уже существует");
        }
        user.setId(userCount);
        users.put(userCount,user);
        userCount++;
        log.info("Добавлен пользователь {}",user);
        return user;
    }

    @Override
    public User updateUser(@Valid User user) {
        /*if(user.getId()==-1){
            throw new ValidationException("Не удалось обновить пользователя: не указан идентификатор");
        }*/
        checkUserValidation(user);
        if(users.containsKey(user.getId())){
            user.setFriendIdList(users.get(user.getId()).getFriendIdList());
            users.replace(user.getId(),user);
            log.info("Обновлен пользователь {}",user);
            return user;
        }
        throw new InstanceNotFoundException("Не удалось обновить пользователя: пользователь не найден.");

    }

    @Override
    public User deleteUser(int id) {
        if(!users.containsKey(id)){
            throw new InstanceNotFoundException("Не удалось удалить пользователя: пользователь не найден.");
        }
        User removingUser = users.get(id);
        users.remove(id);
        log.info("Удален пользователь {}",removingUser);
        return removingUser;
    }

    @Override
    public User getUserById(int id) throws Exception{
        if(!users.containsKey(id)){
            throw new Exception("id = "+id);
        }
        return users.get(id);
    }

    private void checkUserValidation( User user) {
        StringBuilder message = new StringBuilder().append("Не удалось добавить пользователя: ");
        boolean isValid = true;
        if (user.getLogin().contains(" ")) {
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
