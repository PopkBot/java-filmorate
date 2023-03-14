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
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmControllerTest {

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
        ResponseEntity<List> response = restTemplate.getForEntity("/films", List.class);
        System.out.println("Тело ответа: "+response.getBody().toString());
        Assertions.assertEquals((response.getStatusCode()), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldPostAndReturnValidFilm(){

        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description("d1")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1))
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("f2")
                .description("d2")
                .releaseDate(LocalDate.of(2001,1,1))
                .duration(10)
                .mpa(new RatingMPA(1))
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film1);
        ResponseEntity<Film> postResponse=restTemplate.exchange("/films",HttpMethod.POST,entity,Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film1,postResponse.getBody());
        HttpEntity<Film> entity2 = new HttpEntity<>(film2);
        postResponse=restTemplate.exchange("/films",HttpMethod.POST,entity2,Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film2,postResponse.getBody());

        ArrayList<Film> filmList = new ArrayList<>(List.of(film1,film2));
        ResponseEntity<ArrayList<Film>> getResponse = restTemplate.exchange("/films", HttpMethod.GET, null,
                new ParameterizedTypeReference<ArrayList<Film>>() {});
        System.out.println("Тело ответа: "+getResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,getResponse.getStatusCode());
        Assertions.assertEquals(filmList,getResponse.getBody());


    }

    @Test
    void shouldUpdateFilm(){
        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description("d1")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1))
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("f2")
                .description("d2")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1))
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films",film1, Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film1,postResponse.getBody());

        HttpEntity<Film> entity = new HttpEntity<Film>(film2);
        ResponseEntity<Film> putResponse = restTemplate.exchange("/films",HttpMethod.PUT,entity, Film.class);
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
    }

    @Test
    void shouldReturnInstanceAlreadyExistException(){
        Film film1 = Film.builder()
                .id(1)
                .name("f2")
                .description("d1")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .likedUsersId(new HashSet<>())
                .genres(new ArrayList<>())
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films",film1, Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film1,postResponse.getBody());

        ResponseEntity<String> postResponse1 = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.CONFLICT,postResponse1.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: фильм уже существует\"}"
                ,postResponse1.getBody());
    }

    @Test
    void shouldReturnValidationExceptionOnEmptyName(){

        Film film1 = Film.builder()
                .id(1)
                .name(" ")
                .description("d1")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .build();        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,postResponse.getStatusCode());


    }

    @Test
    void shouldReturnValidationExceptionOnTooLongDescription(){

        int descriptionMaxLength=200;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0;i<descriptionMaxLength+10;i++){
            stringBuilder.append("a");
        }
        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description(stringBuilder.toString())
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .build();
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: описание не должно превышать "
                        +descriptionMaxLength+" символов; \"}",postResponse.getBody());
    }

    @Test
    void shouldReturnValidationExceptionOnWrongReleaseDate(){
        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description("d")
                .releaseDate(LocalDate.of(1895,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .build();
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: фильм не мог быть выпущен до рождения кино; \"}"
                ,postResponse.getBody());
    }

    @Test
    void shouldReturnValidationExceptionOnNegativeDuration(){
        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description("d")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(-10)
                .mpa(new RatingMPA(1,"G"))
                .build();
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: длительность фильма должна быть положительной; \"}"
                ,postResponse.getBody());
    }

    @Test
    void shouldReturnExceptionAddingLikeToNotExistingFilm(){
        User user = User.builder()
                .id(1)
                .email("u1@m.ru")
                .login("l1")
                .name("n1")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user);
        restTemplate.exchange("/users",HttpMethod.POST,entity,User.class);
        ResponseEntity<String> putResponse = restTemplate.exchange("/films/1111/like/1",HttpMethod.PUT,null, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND,putResponse.getStatusCode());
    }

    @Test
    void shouldReturnExceptionAddingLikeFromNotExistingUser(){
        Film film = Film.builder()
                .id(1)
                .name("f1")
                .description("d")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film);
        ResponseEntity<Film>postResponse= restTemplate.exchange("/films",HttpMethod.POST,entity,Film.class);
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        ResponseEntity<String> putResponse = restTemplate.exchange("/films/1/like/1111",HttpMethod.PUT,null, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND,putResponse.getStatusCode());
    }


    @Test
    void shouldReturnExceptionWhenDeletingLikeFromNotExistingFilm(){

        String expectedResponse = "{\"message\":\"Фильм с id 1111 не найден.\"}";
        User user = User.builder()
                .id(1)
                .email("u1@m.ru")
                .login("l1")
                .name("n1")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user);
        restTemplate.exchange("/users",HttpMethod.POST,entity,User.class);
        ResponseEntity<String> putResponse = restTemplate.exchange("/films/1111/like/1",HttpMethod.DELETE,null, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND,putResponse.getStatusCode());
        System.out.println(putResponse.getBody());
        Assertions.assertEquals(expectedResponse,putResponse.getBody());
    }

    @Test
    void shouldReturnExceptionWhenDeletingLikeOfNotExistingUser(){
        String expectedResponse = "{\"message\":\"Пользователь с id 1111 не найден.\"}";
        Film film = Film.builder()
                .id(1)
                .name("f1")
                .description("d")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .likedUsersId(new HashSet<>())
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film);
        restTemplate.exchange("/films",HttpMethod.POST,entity,Film.class);
        ResponseEntity<String> putResponse = restTemplate.exchange("/films/1/like/1111",HttpMethod.PUT,null, String.class);
        System.out.println(putResponse.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,putResponse.getStatusCode());
    }

    @Test
    void shouldReturnListOfMostPopularFilms(){
        User user1 = User.builder()
                .id(1)
                .email("u1@m.ru")
                .login("l1")
                .name("n1")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        User user2 = User.builder()
                .id(2)
                .email("u2@m.ru")
                .login("l2")
                .name("n2")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        HttpEntity<User> userEntity;
        userEntity = new HttpEntity<>(user1);
        restTemplate.exchange("/users",HttpMethod.POST,userEntity,User.class);
        userEntity = new HttpEntity<>(user2);
        restTemplate.exchange("/users",HttpMethod.POST,userEntity,User.class);

        Film film1 = Film.builder()
                .id(1)
                .name("f1")
                .description("d1")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .likedUsersId(new HashSet<>(Set.of(1,2)))
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("f2")
                .description("d2")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .likedUsersId(new HashSet<>(Set.of(2)))
                .build();
        Film film3 = Film.builder()
                .id(3)
                .name("f3")
                .description("d3")
                .releaseDate(LocalDate.of(1995,12,27))
                .duration(10)
                .mpa(new RatingMPA(1,"G"))
                .likedUsersId(new HashSet<>())
                .build();
        HttpEntity<Film> filmEntity;
        filmEntity = new HttpEntity<>(film1);
        restTemplate.exchange("/films",HttpMethod.POST,filmEntity,Film.class);
        filmEntity = new HttpEntity<>(film2);
        restTemplate.exchange("/films",HttpMethod.POST,filmEntity,Film.class);
        filmEntity = new HttpEntity<>(film3);
        restTemplate.exchange("/films",HttpMethod.POST,filmEntity,Film.class);

        restTemplate.exchange("/films/1/like/1",HttpMethod.PUT,null, String.class);
        restTemplate.exchange("/films/1/like/2",HttpMethod.PUT,null, String.class);
        restTemplate.exchange("/films/2/like/1",HttpMethod.PUT,null, String.class);

        List<Film> popularFilmsList = new ArrayList<>(List.of(film1,film2,film3));
        ResponseEntity<List<Film>> getResponse = restTemplate.exchange("/films/popular?count=3", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Film>>() {});
        Assertions.assertEquals(HttpStatus.OK,getResponse.getStatusCode());
        Assertions.assertEquals(popularFilmsList,getResponse.getBody());

    }




}