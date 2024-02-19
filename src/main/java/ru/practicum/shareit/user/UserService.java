package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    User getUser(Integer id);

    void removeUser(Integer id);

    User updateUser(User user, Integer userId);
}
