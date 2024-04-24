package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(UserDto user);

    UserDto get(long id);

    void remove(long id);

    UserDto update(UserUpdateDto user);
}
