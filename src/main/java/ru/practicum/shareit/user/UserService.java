package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User save(User user);

    User get(int id);

    void remove(int id);

    User update(User user);
}
