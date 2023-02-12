package ru.yandex.practicum.filmorate.customExceptions;

public class InternalErrorException extends RuntimeException{

    public InternalErrorException(){}

    public InternalErrorException(String message){
        super(message);
    }
    public InternalErrorException(Throwable cause){
        super(cause);
    }
    public InternalErrorException(String message,Throwable cause){
        super(message,cause);
    }
}