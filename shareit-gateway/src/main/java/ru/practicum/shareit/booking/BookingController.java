package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotSupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Создается бронь {}", bookingCreateDto);
        return bookingClient.bookItem(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatusOfBooking(@PathVariable long bookingId,
                                                        @RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                        @RequestParam Boolean approved) {
        log.info("Обновляется статус брони с идентификатором {}", bookingId);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info("Получаем бронь с идентификатором {}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получаем брони пользователя с state {}, userId={}, from={}, size={}", state, userId, from, size);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotSupportedStateException("Unknown state: " + state));
        return bookingClient.getBookings(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwnerItems(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получаем брони владельца вещей с state {}, userId={}, from={}, size={}", state, userId, from, size);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotSupportedStateException("Unknown state: " + state));

        return bookingClient.getBookingsOfOwner(userId, bookingState, from, size);
    }
}
