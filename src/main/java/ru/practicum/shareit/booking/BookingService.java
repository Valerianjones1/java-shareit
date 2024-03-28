package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingDto bookingDto, long userId);

    BookingResponseDto updateStatus(long bookingId, long userId, Boolean approved);

    BookingResponseDto get(long bookingId, long userId);

    List<BookingResponseDto> getAllByUser(long userId, String state);

    List<BookingResponseDto> getAllByOwnerItems(long userId, String state);

}
