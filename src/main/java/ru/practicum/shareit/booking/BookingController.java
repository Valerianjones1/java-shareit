package ru.practicum.shareit.booking;

import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotSupportedStateException;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Создается бронь " + bookingCreateDto);
        return service.create(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusOfBooking(@PathVariable long bookingId,
                                            @RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                            @RequestParam Boolean approved) {
        log.info(String.format("Обновляется статус брони с идентификатором %s", bookingId));
        return service.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Получаем бронь с идентификатором %s", bookingId));
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("Получаем все %s брони пользователя с идентификатором %s", state, userId));
        return service.getAllByUser(userId, getBookingState(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwnerItems(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("Получаем все %s брони владельца вещей с идентификатором %s", state, userId));
        return service.getAllByOwnerItems(userId, getBookingState(state));
    }

    private BookingState getBookingState(String state) {
        if (!Enums.getIfPresent(BookingState.class, state).isPresent()) {
            throw new NotSupportedStateException(String.format("Unknown state: %s", state));
        }
        return BookingState.valueOf(state);
    }
}
