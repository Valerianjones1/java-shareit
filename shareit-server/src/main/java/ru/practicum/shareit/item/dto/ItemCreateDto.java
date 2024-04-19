package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemCreateDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
