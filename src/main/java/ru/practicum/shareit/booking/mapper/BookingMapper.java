package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());

        return bookingDto;
    }

    public static Booking mapToBooking(BookingDto bookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());

        return booking;
    }

    public static BookingResponseDto mapToBookingResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setItem(booking.getItem());
        bookingResponseDto.setBooker(booking.getBooker());
        bookingResponseDto.setStart(booking.getStartDate());
        bookingResponseDto.setEnd(booking.getEndDate());
        bookingResponseDto.setStatus(booking.getStatus());

        return bookingResponseDto;
    }

    public static BookingItemDto mapToBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getBooker().getId());
    }

}
