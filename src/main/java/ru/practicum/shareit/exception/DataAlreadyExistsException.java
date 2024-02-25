package ru.practicum.shareit.exception;

public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException(String message) {
        super(message);
    }

    public DataAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
