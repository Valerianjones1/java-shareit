package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    User save(User user);

    Optional<User> get(int id);

    void remove(int id);

    User update(User user);
}
