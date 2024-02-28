package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;

}