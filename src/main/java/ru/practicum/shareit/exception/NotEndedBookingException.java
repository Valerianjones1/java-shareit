package ru.practicum.shareit.exception;

public class NotEndedBookingException extends RuntimeException {
    public NotEndedBookingException(String message) {
        super(message);
    }
}
