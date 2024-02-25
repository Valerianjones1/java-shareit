package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Item {
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 200)
    private String description;

    private Boolean available;

    private Integer owner;

    private Integer request;
}
