package ru.practicum.shareit.exception;

public class NotRightOwnerException extends RuntimeException {
    public NotRightOwnerException(String message) {
        super(message);
    }

    public NotRightOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
