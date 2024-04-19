package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Получаем всех пользователей");
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("Создаем пользователя {}", user);
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserUpdateDto user,
                              @PathVariable long userId) {
        log.info("Обновляем пользователя {} с идентификатором {}", user, userId);
        user.setId(userId);
        return service.update(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Получаем пользователя с идентификатором {}", userId);
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable long userId) {
        log.info("Удаляем пользователя с идентификатором {}", userId);
        service.remove(userId);
    }

}
