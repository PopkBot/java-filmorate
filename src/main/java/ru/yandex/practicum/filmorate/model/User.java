package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Setter
public class User {

    @EqualsAndHashCode.Exclude
    private int id;
    @Email(message = "неверный формат email")
    @NotBlank(message = "email не может быть пустым")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    private String login;
    private String name;
    @NotNull(message = "дата рождения не может быть пустой")
    private LocalDate birthday;
    @EqualsAndHashCode.Exclude
    private HashSet<Integer> friendIdList;
    @EqualsAndHashCode.Exclude
    private HashMap<Integer,FriendStatus> friendStatuses;

    public User(int id, String email, String login, String name, LocalDate birthday, HashSet<Integer> friendIdList) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendIdList = friendIdList;
    }

    public enum FriendStatus{
        NOT_ACCEPTED,
        ACCEPTED;
    }

}
