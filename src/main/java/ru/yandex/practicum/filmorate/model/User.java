package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;


@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @EqualsAndHashCode.Exclude
    private String name;
    @NotNull(message = "дата рождения не может быть пустой")
    private LocalDate birthday;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private HashSet<Integer> friendIdList=new HashSet<>();
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private HashMap<Integer,FriendStatus> friendStatuses=new HashMap<>();



    public enum FriendStatus{
        NOT_ACCEPTED,
        ACCEPTED;
    }

}
