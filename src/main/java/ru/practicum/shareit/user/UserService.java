package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(UserDto user);

    UserDto get(int id);

    void remove(int id);

    UserDto update(UserDto user);
}
