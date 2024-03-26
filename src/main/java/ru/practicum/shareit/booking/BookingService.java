package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.List;

public interface BookingService {
    BookingUpdateDto create(BookingDto bookingDto, long userId);

    BookingUpdateDto updateStatus(long bookingId, long userId, Boolean approved);

    BookingUpdateDto get(long bookingId, long userId);

    List<BookingUpdateDto> getAllByUser(long userId, String state);

    List<BookingUpdateDto> getAllByOwnerItems(long userId, String state);

}
