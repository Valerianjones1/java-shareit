package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService service;

    @PostMapping()
    public BookingUpdateDto createBooking(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                          @Valid @RequestBody BookingDto bookingDto) {
        return service.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingUpdateDto updateStatusOfBooking(@PathVariable int bookingId,
                                                  @RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                                  @RequestParam Boolean approved) {
        return service.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingUpdateDto getBooking(@PathVariable int bookingId,
                                       @RequestHeader(CUSTOM_USER_ID_HEADER) int userId) {
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingUpdateDto> getBookingsOfUser(@RequestHeader(CUSTOM_USER_ID_HEADER) int userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingUpdateDto> getBookingsOfOwnerItems(@RequestHeader(CUSTOM_USER_ID_HEADER) int userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllByOwnerItems(userId, state);
    }
}
