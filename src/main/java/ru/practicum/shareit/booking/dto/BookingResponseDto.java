package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.Date;

import java.time.LocalDateTime;

@Data
@Date
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BookingResponseDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingState status;

    private User booker;

    private Item item;
}
