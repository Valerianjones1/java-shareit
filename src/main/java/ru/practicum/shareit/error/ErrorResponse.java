package ru.practicum.shareit.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final String description;
    private String stacktrace;
}