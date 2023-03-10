package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userCount=1;

    /**
     * Возвращает таблицу всех пользователей
     * @return - HashMap<Integer,User>
     */
    @Override
    public HashMap<Integer,User> getAllUsers(){
        log.info("Запрошен список всех пользователей");
        return users;
    }

    /**
     * Добавляет пользователя в таблицу
     * @param user User добавляемый пользователь
     * @return - User в случае успешного добавления пользователя возвращает добавленный объект
     */
    @Override
    public User addUser(User user) {
        checkUserValidation(user);
        if(users.containsValue(user)){
            throw new InstanceAlreadyExistException("Не удалось добавить пользователя: пользователь уже существует");
        }
        user.setId(userCount);
        user.setFriendIdList(new HashSet<>());
        users.put(userCount,user);
        userCount++;
        log.info("Добавлен пользователь {}",user);
        return user;
    }

    /**
     * Обновляет пользователя в таблице
     * @param user обновленная версия пользователя, содержит идентификатор Id
     * @return - User в случае успешного обновления пользователя возвращает добавленный объект
     */
    @Override
    public User updateUser(User user) {

        checkUserValidation(user);
        if(users.containsKey(user.getId())){
            user.setFriendIdList(users.get(user.getId()).getFriendIdList());
            users.replace(user.getId(),user);
            log.info("Обновлен пользователь {}",user);
            return user;
        }
        throw new DataNotFoundException("Не удалось обновить пользователя: пользователь не найден.");

    }

    /**
     * Удаляет пользователя с идентификатором id из таблицы
     * @param id идентификатор пользователя, которого необходимо удалить
     * @return - User копия удаленного пользователя возвращается в случае успешного удаления из таблицы
     */
    @Override
    public User deleteUser(int id) {
        if(!users.containsKey(id)){
            throw new DataNotFoundException("Не удалось удалить пользователя: пользователь не найден.");
        }
        User removingUser = users.get(id);
        users.remove(id);
        log.info("Удален пользователь {}",removingUser);
        return removingUser;
    }

    /**
     * Удаляет всех пользователей из таблицы, восстанавливает счетчик идентификаторов
     */
    @Override
    public void deleteAllUsers() {
        userCount=1;
        users.clear();
        log.info("Список пользователей очищен");
    }

    /**
     * Возвращает пользователя по идентификатору
     * @param id идентификатор пользователя, которого необходимо передать
     * @return User пользователь с запрошенным id
     * @throws Exception - пользователь с указанным id не найден в таблице
     */
    @Override
    public User getUserById(int id){
        if(!users.containsKey(id)){
            throw new DataNotFoundException("Пользователь с id "+id+" не найден.");
        }
        log.info("Передан пользователь id = {}",id);
        return users.get(id);
    }

    /**
     * Проверяет поля пользователя на корректность
     * @param user пользователь, чьи поля необходимо проверить
     */
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
