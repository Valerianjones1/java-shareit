package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private final Integer id;

    private final Integer userId;

    @NotNull
    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDateTime created;
}
