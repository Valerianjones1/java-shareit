package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Date;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Date
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
