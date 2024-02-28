package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> findAllUsers() {
        return service.getAll();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto user,
                              @PathVariable int userId) {
        user.setId(userId);
        return service.update(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable int userId) {
        service.remove(userId);
    }

}
