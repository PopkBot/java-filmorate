package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @DeleteMapping("/users/{id}")
    public User deleteAllUsers(@PathVariable int id){
        log.info("Запрос: удалить пользователя с id {}",id);
        return userService.deleteUserById(id);
    }

    @DeleteMapping("/users")
    public void deleteUsers(){
        log.info("Запрос: удалить всех пользователей");
        userService.deleteAllUsers();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user){
        log.info("Запрос: добавить пользователя {}",user);
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){
        log.info("Запрос: обновить пользователя {}",user);
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers(){
        log.info("Запрос: передать всех пользователей");
        return userService.getAllUsers().values();
    }

    @GetMapping("/users/{id}")
    public User  getUserById(@PathVariable int id){
        log.info("Запрос: передать пользователя с id {}",id);
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable int id,@PathVariable int friendId){
        log.info("Запрос: сделать пользователей {} и {} друзьями",id,friendId);
        userService.makeFriends(id,friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,@PathVariable int friendId){
        log.info("Запрос: удалить пользователей из друзей {} и {}",id,friendId);
        userService.deleteFriend(id,friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id){
        log.info("Запрос: передать всех друзей пользователя {}",id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId){
        log.info("Запрос: передать общих друзей пользователей {} и {}",id,otherId);
        return userService.getMutualFriends(id,otherId);
    }

}
