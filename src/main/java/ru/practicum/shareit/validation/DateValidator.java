package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingCreateDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<Date, BookingCreateDto> {

    private String message;

    @Override
    public void initialize(final Date constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final BookingCreateDto bookingCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime startDate = bookingCreateDto.getStart();
        LocalDateTime endDate = bookingCreateDto.getEnd();
        return (startDate != null && endDate != null)
                && (startDate.isBefore(endDate) || endDate.isAfter(startDate))
                && (startDate.isEqual(LocalDateTime.now()) || startDate.isAfter(LocalDateTime.now()));
    }
}
