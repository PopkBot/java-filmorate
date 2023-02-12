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
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @DeleteMapping("/users")
    public void deleteAllUsers(){
        userService.deleteAllUsers();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers(){
        return userService.getAllUsers().values();
    }

    @GetMapping("/users/{id}")
    public User  getUserById(@PathVariable int id){
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable int id,@PathVariable int friendId){
        userService.makeFriends(id,friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,@PathVariable int friendId){
        userService.deleteFriend(id,friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id){
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId){
        return userService.getMutualFriends(id,otherId);
    }




}
