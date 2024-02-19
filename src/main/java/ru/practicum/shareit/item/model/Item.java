package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private final Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @Size(max=200)
    private String description;

    private boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;
}
