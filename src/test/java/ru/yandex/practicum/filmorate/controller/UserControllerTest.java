package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
        restTemplate.delete("/films");
        restTemplate.delete("/users");
    }
    @AfterEach
    void afterEach(){
        restTemplate.delete("/films");
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


        User user1 = new User(1,"u1@m.ru","l1","n1",LocalDate.of(2000,1,1),new HashSet<>());
        User user2 = new User(2,"u2@m.ru","l2","n2",LocalDate.of(2002,1,1),new HashSet<>());

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
    void shouldReturnInstanceAlreadyExistException(){
        User user1 = new User(1,"u1@m.ru","l1","n1",LocalDate.of(2000,1,1),new HashSet<>());
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

        User user1 = new User(1,"u1mru","l1","n1",LocalDate.of(2000,1,1),new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users",user1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,postResponse.getStatusCode());

        user1 = new User(1,"","l1","n1",LocalDate.of(2000,1,1),new HashSet<>());
        postResponse = restTemplate.postForEntity("/users",user1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,postResponse.getStatusCode());
    }

    @Test
    void shouldNotAcceptInvalidLogin() {

        User user1 = new User(1, "u1@m.ru", "", "n1", LocalDate.of(2000, 1, 1),new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, postResponse.getStatusCode());


        user1 = new User(1, "u1@m.ru", "a a", "n1", LocalDate.of(2000, 1, 1),new HashSet<>());
        postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить пользователя: неверный формат логина; \"}"
                , postResponse.getBody());
    }

    @Test
    void shouldUseLoginIfNameIsBlank(){
        User user1 = new User(1, "u1@m.ru", "l1", " ", LocalDate.of(2000, 1, 1),new HashSet<>());
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user1, User.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        Assertions.assertEquals("l1",postResponse.getBody().getName());
    }

    @Test
    void shouldNotAcceptWrongBirthDate(){
        User user1 = new User(1, "u1@m.ru", "l1", "n1", LocalDate.now().plusYears(1),new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/users", user1, String.class);
        System.out.println("Тело ответа: " + postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить пользователя: пользователь из будущего; \"}"
                , postResponse.getBody());
    }

    @Test
    void shouldNotAddFriendSameUser(){

        User user1 = new User(1, "u1@m.ru", "l1", " ", LocalDate.of(2000, 1, 1),new HashSet<>());
        HttpEntity<User> userEntity = new HttpEntity<>(user1);
        restTemplate.exchange("/users",HttpMethod.POST,userEntity,User.class);
        ResponseEntity<String> putResponse = restTemplate.exchange("/users/1/friends/1",HttpMethod.PUT,null,String.class);
        System.out.println(putResponse.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,putResponse.getStatusCode());

    }

    @Test
    void shouldNotAddNotExistingFriend(){
        User user1 = new User(1, "u1@m.ru", "l1", " ", LocalDate.of(2000, 1, 1),new HashSet<>());
        HttpEntity<User> userEntity = new HttpEntity<>(user1);
        restTemplate.exchange("/users",HttpMethod.POST,userEntity,User.class);
        ResponseEntity<String> putResponse = restTemplate.exchange("/users/1/friends/2222",HttpMethod.PUT,null,String.class);
        System.out.println(putResponse.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,putResponse.getStatusCode());
    }



    @Test
    void shouldThrowExceptionWhenDeletingNonExistingFriend(){

        User user1 = new User(1, "u1@m.ru", "l1", " ", LocalDate.of(2000, 1, 1),new HashSet<>());

        HttpEntity<User> userEntity = new HttpEntity<>(user1);
        restTemplate.exchange("/users",HttpMethod.POST,userEntity,User.class);
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/users/1/friends/222",HttpMethod.DELETE,null,String.class);
        System.out.println(deleteResponse.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,deleteResponse.getStatusCode());
    }





}