package ru.yandex.practicum.filmorate.customExceptions;

public class InstanceAlreadyExistException extends RuntimeException{

    public InstanceAlreadyExistException(){}

    public InstanceAlreadyExistException(String message){
        super(message);
    }
    public InstanceAlreadyExistException(Throwable cause){
        super(cause);
    }
    public InstanceAlreadyExistException(String message,Throwable cause){
        super(message,cause);
    }
}
