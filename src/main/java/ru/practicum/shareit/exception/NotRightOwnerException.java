package ru.practicum.shareit.exception;

public class NotRightOwnerException extends RuntimeException {
    public NotRightOwnerException(String message) {
        super(message);
    }
}
