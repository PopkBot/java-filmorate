package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.InternalErrorException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage=userStorage;
    }

    public HashMap<Integer,User> getAllUsers(){
        return userStorage.getAllUsers();
    }

    public User getUserById(int id){
        try {
            return userStorage.getUserById(id);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Пользователь с "+e.getMessage()+" не найден.");
        }
    }

    public void deleteAllUsers(){
        userStorage.deleteAllUsers();
    }

    public User addUser(User user){
        return userStorage.addUser(user);
    }

    public User updateUser(User user){
        return userStorage.updateUser(user);
    }

    public User deleteUserById(int userId){
        return userStorage.deleteUser(userId);
    }

    public void makeFriends(int user1Id,int user2Id){

        if(user1Id==user2Id){
            throw new ValidationException("Пользователь не может добавить себя в друзья :(");
        }
        User user1;
        User user2;
        try {
            user1=userStorage.getUserById(user1Id);
            user2=userStorage.getUserById(user2Id);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось добавить в друзья: пользователь с "+e.getMessage()+" не найден.");
        }
        user1.getFriendIdList().add(user2Id);
        user2.getFriendIdList().add(user1Id);
        log.info("{} и {} стали друзьями",user1,user2);
    }

    public void deleteFriend(int user1Id, int user2Id){

        User user1;
        User user2;
        try {
            user1=userStorage.getUserById(user1Id);
            user2=userStorage.getUserById(user2Id);
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось удалить из друзей: пользователь с "+e.getMessage()+" не найден.");
        }
        user1.getFriendIdList().remove(user2Id);
        user2.getFriendIdList().remove(user1Id);
        log.info("{} и {} больше не друзья",user1,user2);
    }

    public List<User> getFriends(int userId){

        HashSet<Integer> friendIdList;
        List<User> friendList = new ArrayList<>();
        HashMap<Integer,User> userList = userStorage.getAllUsers();
        try {
            friendIdList = userStorage.getUserById(userId).getFriendIdList();
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось найти друзей: пользователь с "+e.getMessage()+" не найден.");
        }
        for(int id:friendIdList){
            friendList.add(userList.get(id));
        }
        log.info("Запрошен список друзей пользователя id = {}",userId);
        return friendList;
    }

    public List<User> getMutualFriends(int user1Id,int user2Id){

        if(user1Id==user2Id){
            throw new ValidationException("Пользователь разделяет с собой всех своих друзей :D");
        }
        HashSet<Integer> user1FriendList;
        HashSet<Integer> user2FriendList;
        try {
            user1FriendList=userStorage.getUserById(user1Id).getFriendIdList();
            user2FriendList=userStorage.getUserById(user2Id).getFriendIdList();
        } catch (Exception e) {
            throw new InstanceNotFoundException("Не удалось найти общих друзей: пользователь с "+e.getMessage()+" не найден.");
        }
        List<User> mutualFriends = new ArrayList<>();

        for(int i: user1FriendList){
            if(user2FriendList.contains(i)){
                try {
                    mutualFriends.add(userStorage.getUserById(i));
                } catch (Exception e) {
                    throw new InternalErrorException("Что-то пошло не так :Р");
                }
            }
        }
        log.info("Запрошен список общих друзей пользователей с id {} и {}",user1Id,user2Id);
        return mutualFriends;


    }

}
