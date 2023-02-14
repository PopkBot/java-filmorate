package ru.yandex.practicum.filmorate.customExceptions;

public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(){}

    public DataNotFoundException(String message){
        super(message);
    }
    public DataNotFoundException(Throwable cause){
        super(cause);
    }
    public DataNotFoundException(String message, Throwable cause){
        super(message,cause);
    }
}
