package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestCreateDto {
    @NotNull
    @NotBlank
    private String description;
}
