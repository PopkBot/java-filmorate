package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.InternalErrorException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Service
public class UserService {

    private InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage){
        this.userStorage=userStorage;
    }


    public void makeFriends(int user1Id,int user2Id){

        if(user1Id==user2Id){
            throw new ValidationException("Пользователь не может добавить себя в друзья :(");
        }
        User user1;
        User user2;
        try {
            user1=userStorage.getUser(user1Id);
            user2=userStorage.getUser(user2Id);
        } catch (Exception e) {
            throw new ValidationException("Не удалось добавить в друзья: пользователь с "+e.getMessage()+" не найден.");
        }
        user1.getFriendIdList().add(user2Id);
        user2.getFriendIdList().add(user1Id);
    }

    public void deleteFriend(int user1Id, int user2Id){

        User user1;
        User user2;
        try {
            user1=userStorage.getUser(user1Id);
            user2=userStorage.getUser(user2Id);
        } catch (Exception e) {
            throw new ValidationException("Не удалось удалить из друзей: пользователь с "+e.getMessage()+" не найден.");
        }
        user1.getFriendIdList().remove(user2Id);
        user2.getFriendIdList().remove(user1Id);
    }

    public ArrayList<User> getMutualFriends(int user1Id,int user2Id){

        if(user1Id==user2Id){
            throw new ValidationException("Пользователь разделяет с собой всех своих друзей :D");
        }
        HashSet<Integer> user1FriendList;
        HashSet<Integer> user2FriendList;
        try {
            user1FriendList=userStorage.getUser(user1Id).getFriendIdList();
            user2FriendList=userStorage.getUser(user2Id).getFriendIdList();
        } catch (Exception e) {
            throw new ValidationException("Не удалось найти общих друзей: пользователь с "+e.getMessage()+" не найден.");
        }
        ArrayList<User> mutualFriends = new ArrayList<>();

        for(int i: user1FriendList){
            if(user2FriendList.contains(i)){
                try {
                    mutualFriends.add(userStorage.getUser(i));
                } catch (Exception e) {
                    throw new InternalErrorException("Что-то пошло не так :Р");
                }
            }
        }
        return mutualFriends;


    }

}
