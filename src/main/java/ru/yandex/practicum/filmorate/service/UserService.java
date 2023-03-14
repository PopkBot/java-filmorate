package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

@Service()
@Slf4j
public class UserService {

    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
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
        userStorage.makeFriends(user1Id,user2Id);
        log.info("{} и {} стали друзьями", user1Id, user2Id);
    }

    /**
     * Удаляет идентификаторы пользователей из их списка идентификаторов друзей
     *
     * @param user1Id идентификатор пользователя, из чьего списка друзей удалится пользователь с идентификатором user2Id
     * @param user2Id идентификатор пользователя, из чьего списка друзей удалится пользователь с идентификатором user1Id
     */
    public void deleteFriend(int user1Id, int user2Id) {

        userStorage.deleteFriends(user1Id,user2Id);
        log.info("{} и {} больше не друзья", user1Id, user2Id);
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
        friendIdList = userStorage.getUserById(userId).getFriendIdList();
        for (int id : friendIdList) {
            friendList.add(userStorage.getUserById(id));
        }
        Collections.sort(friendList,(f1,f2)->f1.getId()-f2.getId());
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
