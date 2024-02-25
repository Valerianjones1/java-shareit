package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> findAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        return service.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user,
                           @PathVariable Integer userId) {
        return service.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return service.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Integer userId) {
        service.removeUser(userId);
    }

}
