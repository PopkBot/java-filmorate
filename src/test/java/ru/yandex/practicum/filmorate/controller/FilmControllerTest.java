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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

        Film film1 = new Film(1,"f1","d1",LocalDate.of(2000,1,1),10,new HashSet<>());
        Film film2 = new Film(2,"f2","d2",LocalDate.of(2001,1,1),10,new HashSet<>());

        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films",film1, Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film1,postResponse.getBody());
        postResponse = restTemplate.postForEntity("/films",film2, Film.class);
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
        Film film1 = new Film(1,"f1","d1",LocalDate.of(2000,1,1),10,new HashSet<>());
        Film film2 = new Film(1,"f2","d2",LocalDate.of(2001,1,1),10,new HashSet<>());
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films",film1, Film.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film1,postResponse.getBody());

        HttpEntity<Film> entity = new HttpEntity<Film>(film2);
        ResponseEntity<Film> putResponse = restTemplate.exchange("/films",HttpMethod.PUT,entity, Film.class);
        Assertions.assertEquals(HttpStatus.OK,postResponse.getStatusCode());
        Assertions.assertEquals(film2,putResponse.getBody());

    }

    @Test
    void shouldReturnIntanceAlreadyExistException(){
        Film film1 = new Film(1,"f1","d1",LocalDate.of(2000,1,1),10,new HashSet<>());
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

        Film film1 = new Film(1," ","d1",LocalDate.of(2000,1,1),10,new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
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
        Film film1 = new Film(1,"a",stringBuilder.toString(),LocalDate.of(2000,1,1),10,new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: описание не должно превышать "
                        +descriptionMaxLength+" символов; \"}",postResponse.getBody());
    }

    @Test
    void shouldReturnValidationExceptionOnWrongReleaseDate(){
        Film film1 = new Film(1,"n","d",LocalDate.of(1895,12,27),10,new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: фильм не мог быть выпущен до рождения кино; \"}"
                ,postResponse.getBody());
    }

    @Test
    void shouldReturnValidationExceptionOnNegativeDuration(){
        Film film1 = new Film(1,"n","d",LocalDate.of(1896,12,27),-10,new HashSet<>());
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/films",film1, String.class);
        System.out.println("Тело ответа: "+postResponse.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,postResponse.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Не удалось добавить фильм: длительность фильма должна быть положительной; \"}"
                ,postResponse.getBody());
    }

}