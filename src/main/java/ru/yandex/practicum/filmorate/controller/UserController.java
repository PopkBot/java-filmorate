package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping
@Slf4j
public class UserController {

    private InMemoryUserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage,UserService userService){
        this.userService=userService;
        this.userStorage=userStorage;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }


    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {


        return user;
    }



    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){

            return user;
    }


}
