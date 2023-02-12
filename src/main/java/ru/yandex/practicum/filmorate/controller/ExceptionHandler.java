package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.customExceptions.InstanceAlreadyExistException;
import ru.yandex.practicum.filmorate.customExceptions.InstanceNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.ExceptionMessage;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage handleValidationException(final ValidationException ex){
        log.info(ex.getMessage());
        return new ExceptionMessage(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionMessage handleInstanceAlreadyExistException(final InstanceAlreadyExistException ex){
        log.info(ex.getMessage());
        return new ExceptionMessage(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionMessage handleInstanceNotFoundException(final InstanceNotFoundException ex){
        log.info(ex.getMessage());
        return new ExceptionMessage(ex.getMessage());
    }
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionMessage errorMessage(final Throwable ex){
        log.info(ex.getMessage());
        return new ExceptionMessage(ex.getMessage());
    }



}
