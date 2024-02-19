package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User save(User user);

    User get(Integer id);

    void remove(Integer id);

    User update(User user);
}
