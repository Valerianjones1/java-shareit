package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserUpdateDto {
    private Integer id;

    private String name;

    @Email
    private String email;
}
