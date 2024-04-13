package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static Booking mapToBooking(BookingCreateDto bookingCreateDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingCreateDto.getId());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartDate(bookingCreateDto.getStart());
        booking.setEndDate(bookingCreateDto.getEnd());

        return booking;
    }

    public static Booking mapToBooking(BookingCreateDto bookingCreateDto) {
        Booking booking = new Booking();
        booking.setId(bookingCreateDto.getId());;
        booking.setStartDate(bookingCreateDto.getStart());
        booking.setEndDate(bookingCreateDto.getEnd());

        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());

        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static BookingItemDto mapToBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getBooker().getId());
    }

}
