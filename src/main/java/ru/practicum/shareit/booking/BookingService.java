package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, long userId);

    BookingDto updateStatus(long bookingId, long userId, Boolean approved);

    BookingDto get(long bookingId, long userId);

    List<BookingDto> getAllByUser(long userId, BookingState state);

    List<BookingDto> getAllByOwnerItems(long userId, BookingState state);

}
