package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.error("Пользователь или вещь не найдена");
        return new ErrorResponse(e.getMessage(), "Пользователь или вещь не найдена");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataAlreadyExists(final DataAlreadyExistsException e) {
        log.error("Пользователь уже существует");
        return new ErrorResponse(e.getMessage(), "Пользователь уже существует");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error("Ошибка с валидацией");
        return new ErrorResponse(e.getMessage(), "Ошибка с валидацией");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValid(final MethodArgumentNotValidException e) {
        log.error("Ошибка с валидацией");
        return new ErrorResponse(e.getMessage(), "Ошибка с валидацией");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerException(final NotRightOwnerException e) {
        log.error("Запрещено редактировать вещь не владельцу");
        return new ErrorResponse(e.getMessage(), "Запрещено редактировать вещь не владельцу");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotEndedBooking(final NotEndedBookingException e) {
        log.error("Комментировать можно только брони у которых срок закончен");
        return new ErrorResponse(e.getMessage(), "Комментировать можно только брони у которых срок закончен");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableItem(final NotAvailableItemException e) {
        log.error("Запрещено бронировать недоступную вещь");
        return new ErrorResponse(e.getMessage(), "Запрещено бронировать недоступную вещь");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotSupportedState(final NotSupportedStateException e) {
        log.error("Состояние не поддерживается");
        return new ErrorResponse(e.getMessage(), "Состояние не поддерживается");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyApprovedException(final AlreadyApprovedException e) {
        log.error("Бронь уже подтверждена");
        return new ErrorResponse(e.getMessage(), "Бронь уже подтверждена");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка", e);
        return new ErrorResponse("Произошла непредвиденная ошибка", e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
}