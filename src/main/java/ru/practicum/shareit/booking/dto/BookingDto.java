package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Date;

import java.time.LocalDateTime;

@Data
@Date
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private UserDto booker;

    private ItemDto item;
}
