package ru.practicum.shareit.exception;

public class NotRightBookerOrOwnerException extends RuntimeException {
    public NotRightBookerOrOwnerException(String message) {
        super(message);
    }
}
