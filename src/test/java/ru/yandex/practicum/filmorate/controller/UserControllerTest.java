package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private static Integer port;

    @BeforeAll
    static void beforeAll(){
        System.out.println("Server port "+port);
    }
    @BeforeEach
    void beforeEach(){
        restTemplate.delete("/users");
    }

    @Test
    void shouldReturnEmptyList(){
        ResponseEntity<List> response = restTemplate.getForEntity("/users", List.class);
        System.out.println("Тело ответа: "+response.getBody().toString());
        Assertions.assertEquals((response.getStatusCode()), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldPostAndReturnValidUser(){


        User user1 = new User(1,"u1@m.ru","l1","n1",LocalDate.of(2000,1,1));
        User user2 = new User(2,"u2@m.ru","l2","n2",LocalDate.of(2002,1,1));

        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users",user1, User.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(user1,postResponse.getBody());
        postResponse = restTemplate.postForEntity("/users",user2, User.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(user2,postResponse.getBody());

        ArrayList<User> userList = new ArrayList<>(List.of(user1,user2));
        ResponseEntity<ArrayList<User>> getResponse = restTemplate.exchange("/users", HttpMethod.GET, null,
                new ParameterizedTypeReference<ArrayList<User>>() {});
        System.out.println("Тело ответа: "+getResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,getResponse.getStatusCode());
        Assertions.assertEquals(userList,getResponse.getBody());


    }

    @Test
    void shouldUpdateUser(){
        User user1 = new User(1,"u1@m.ru","l1","n1",LocalDate.of(2000,1,1));
        User user2 = new User(1,"u2@m.ru","l2","n2",LocalDate.of(2002,1,1));
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users",user1, User.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(user1,postResponse.getBody());

        HttpEntity<User> entity = new HttpEntity<User>(user2);
        ResponseEntity<User> putResponse = restTemplate.exchange("/users",HttpMethod.PUT,entity, User.class);
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(user2,putResponse.getBody());

    }

    @Test
    void shouldReturnIntanceAlreadyExistException(){
        User user1 = new User(1,"u1@m.ru","l1","n1",LocalDate.of(2000,1,1));
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users",user1, User.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(user1,postResponse.getBody());

        ResponseEntity<String> postResponse1 = restTemplate.postForEntity("/users",user1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.CONFLICT,postResponse1.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить пользователя: пользователь уже существует\"}"
                ,postResponse1.getBody());
    }

    @Test
    void shouldNotAcceptInvalidEmail(){

        User user1 = new User(1,"u1mru","l1","n1",LocalDate.of(2000,1,1));
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users",user1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,postResponse.getStatusCode());

        user1 = new User(1,"","l1","n1",LocalDate.of(2000,1,1));
        postResponse = restTemplate.postForEntity("/users",user1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,postResponse.getStatusCode());
    }

    @Test
    void shouldNotAcceptInvalidLogin() {

        User user1 = new User(1, "u1@m.ru", "", "n1", LocalDate.of(2000, 1, 1));
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, postResponse.getStatusCode());


        user1 = new User(1, "u1@m.ru", "a a", "n1", LocalDate.of(2000, 1, 1));
        postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить пользователя: неверный формат логина; \"}"
                , postResponse.getBody());
    }

    @Test
    void shouldUseLoginIfNameIsBlank(){
        User user1 = new User(1, "u1@m.ru", "l1", " ", LocalDate.of(2000, 1, 1));
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user1, User.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        Assertions.assertEquals("l1",postResponse.getBody().getName());
    }

    @Test
    void shouldNotAcceptWrodBirthDate(){
        User user1 = new User(1, "u1@m.ru", "l1", "n1", LocalDate.now().plusYears(1));
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить пользователя: пользователь из будущего; \"}"
                , postResponse.getBody());
    }
}