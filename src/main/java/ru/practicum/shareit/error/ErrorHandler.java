package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotRightOwnerException;
import ru.practicum.shareit.exception.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.error("Пользователь или вещь не найдена", e);
        return new ErrorResponse(e.getMessage(), "Пользователь или вещь не найдена");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataAlreadyExists(final DataAlreadyExistsException e) {
        log.error("Пользователь уже существует", e);
        return new ErrorResponse(e.getMessage(), "Пользователь уже существует");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error("Ошибка с валидацией", e);
        return new ErrorResponse(e.getMessage(), "Ошибка с валидацией");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValid(final MethodArgumentNotValidException e) {
        log.error("Ошибка с валидацией", e);
        return new ErrorResponse(e.getMessage(), "Ошибка с валидацией");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerException(final NotRightOwnerException e) {
        log.error("Запрещено редактировать вещь не владельцу", e);
        return new ErrorResponse(e.getMessage(), "Запрещено редактировать вещь не владельцу");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка", e);
        return new ErrorResponse("Произошла непредвиденная ошибка", e.getMessage(), ExceptionUtils.getStackTrace(e));
    }

}