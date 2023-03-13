package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserDbStorage {

    public User getUserById(int id);

    public HashMap<Integer,User> getAllUsers();

    public User addUser(User film);

    public User updateUser(User film);

    public User deleteUser(int id);

    public void deleteAllUsers();

    public void makeFriends(int userId, int friendId);

    public void deleteFriends(int userId, int friendId);

    public boolean isPresentInDataBase(User user);

    public boolean isPresentInDataBase(int userId);
}
