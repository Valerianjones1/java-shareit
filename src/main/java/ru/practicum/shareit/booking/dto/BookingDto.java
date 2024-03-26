package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Date;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Date
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
