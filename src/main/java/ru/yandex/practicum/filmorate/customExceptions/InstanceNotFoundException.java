package ru.yandex.practicum.filmorate.customExceptions;

public class InstanceNotFoundException extends RuntimeException{

    public InstanceNotFoundException(){}

    public InstanceNotFoundException(String message){
        super(message);
    }
    public InstanceNotFoundException(Throwable cause){
        super(cause);
    }
    public InstanceNotFoundException(String message,Throwable cause){
        super(message,cause);
    }
}
