package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    public User addUser(User film);

    public User updateUser(User film);

    public User deleteUser(int id);
}
