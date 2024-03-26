package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<Date, BookingDto> {

    private String message;

    @Override
    public void initialize(final Date constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime startDate = bookingDto.getStart();
        LocalDateTime endDate = bookingDto.getEnd();
        return (startDate != null && endDate != null)
                && (startDate.isBefore(endDate) || endDate.isAfter(startDate))
                && (startDate.isEqual(LocalDateTime.now()) || startDate.isAfter(LocalDateTime.now()));
    }
}
