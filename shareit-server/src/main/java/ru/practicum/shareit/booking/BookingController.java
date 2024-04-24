package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

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
                                    @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Создается бронь {}", bookingCreateDto);
        return service.create(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusOfBooking(@PathVariable long bookingId,
                                            @RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                            @RequestParam Boolean approved) {
        log.info("Обновляется статус брони с идентификатором {}", bookingId);
        return service.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info("Получаем бронь с идентификатором {}", bookingId);
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                              @RequestParam BookingState state,
                                              @RequestParam int from,
                                              @RequestParam int size) {
        log.info("Получаем брони пользователя с state {}, userId={}, from={}, size={}", state, userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("startDate").descending());
        return service.getAllByUser(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwnerItems(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                    @RequestParam BookingState state,
                                                    @RequestParam int from,
                                                    @RequestParam int size) {
        log.info("Получаем брони владельца вещей с state {}, userId={}, from={}, size={}", state, userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("startDate").descending());
        return service.getAllByOwnerItems(userId, state, pageable);
    }
}
