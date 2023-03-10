package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.InternalErrorException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public HashMap<Integer, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User deleteUserById(int userId) {
        return userStorage.deleteUser(userId);
    }

    /**
     * Добавляет идентификаторы пользователей в их списки друзей
     *
     * @param user1Id идентификатор пользователя, который запишется в список идентификаторов-друзей пользователя
     *                с идентификатором user2Id
     * @param user2Id идентификатор пользователя, который запишется в список идентификаторов-друзей пользователя
     *                с идентификатором user1Id
     */
    public void makeFriends(int user1Id, int user2Id) {

        if (user1Id == user2Id) {
            throw new ValidationException("Пользователь не может добавить себя в друзья :(");
        }
        User user1;
        User user2;
        user1 = userStorage.getUserById(user1Id);
        user2 = userStorage.getUserById(user2Id);
        user1.getFriendIdList().add(user2Id);
        user2.getFriendIdList().add(user1Id);
        log.info("{} и {} стали друзьями", user1, user2);
    }

    /**
     * Удаляет идентификаторы пользователей из их списка идентификаторов друзей
     *
     * @param user1Id идентификатор пользователя, из чьего списка друзей удалится пользователь с идентификатором user2Id
     * @param user2Id идентификатор пользователя, из чьего списка друзей удалится пользователь с идентификатором user1Id
     */
    public void deleteFriend(int user1Id, int user2Id) {

        User user1;
        User user2;
        user1 = userStorage.getUserById(user1Id);
        user2 = userStorage.getUserById(user2Id);
        user1.getFriendIdList().remove(user2Id);
        user2.getFriendIdList().remove(user1Id);
        log.info("{} и {} больше не друзья", user1, user2);
    }

    /**
     * Возвращает список пользователей с идентификаторами, указанными в списке друзей пользователя с указанным идентификатором
     *
     * @param userId идентификатор пользователя, чьих друзей необходимо передать
     * @return List<User> список друзей
     */
    public List<User> getFriends(int userId) {

        HashSet<Integer> friendIdList;
        List<User> friendList = new ArrayList<>();
        HashMap<Integer, User> userList = userStorage.getAllUsers();
        friendIdList = userStorage.getUserById(userId).getFriendIdList();
        for (int id : friendIdList) {
            friendList.add(userList.get(id));
        }
        log.info("Передан список друзей пользователя id = {}", userId);
        return friendList;
    }

    /**
     * Возвращает список пользователей с идентификаторами, общими для списков друзей пользователей user1Id и use2Id
     *
     * @param user1Id идентификатор 1-го пользователя
     * @param user2Id идентификатор 2-го пользователя
     * @return List<User> список друзей
     */
    public List<User> getMutualFriends(int user1Id, int user2Id) {

        if (user1Id == user2Id) {
            throw new ValidationException("Пользователь разделяет с собой всех своих друзей :D");
        }
        HashSet<Integer> user1FriendList = new HashSet<>(userStorage.getUserById(user1Id).getFriendIdList());
        HashSet<Integer> user2FriendList = userStorage.getUserById(user2Id).getFriendIdList();
        user1FriendList.retainAll(user2FriendList);
        List<User> mutualFriends = new ArrayList<>();
        for (int i : user1FriendList) {
            mutualFriends.add(userStorage.getUserById(i));
        }

        log.info("Передан список общих друзей пользователей с id {} и {}", user1Id, user2Id);
        return mutualFriends;


    }

}
