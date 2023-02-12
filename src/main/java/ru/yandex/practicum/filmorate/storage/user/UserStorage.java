package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {

    public User getUserById(int id) throws Exception;

    public HashMap<Integer,User> getAllUsers();

    public User addUser(User film);

    public User updateUser(User film);

    public User deleteUser(int id);

    public void deleteAllUsers();
}
