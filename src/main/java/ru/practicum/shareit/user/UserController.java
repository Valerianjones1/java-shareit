package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> findAllUsers() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Создаем пользователя " + user);
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody UserUpdateDto user,
                              @PathVariable long userId) {
        log.info(String.format("Обновляем пользователя %s с идентификатором %s", user, userId));
        user.setId(userId);
        return service.update(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info(String.format("Получаем пользователя с идентификатором %s", userId));
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable long userId) {
        log.info(String.format("Удаляем пользователя с идентификатором %s", userId));
        service.remove(userId);
    }

}
