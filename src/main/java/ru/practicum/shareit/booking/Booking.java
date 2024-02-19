package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private final Integer id;

    private LocalDate start;
    private LocalDate end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private Status status;

}
