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
        return service.getAll();
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        return service.save(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user,
                           @PathVariable int userId) {
        user.setId(userId);
        return service.update(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable int userId) {
        service.remove(userId);
    }

}
