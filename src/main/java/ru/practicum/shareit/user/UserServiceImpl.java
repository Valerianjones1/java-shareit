package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    @Override
    public List<User> getAllUsers() {
        return repo.getAll();
    }

    @Override
    public User saveUser(User user) {
        if (isEmailDuplicate(user.getEmail())) {
            throw new ValidationException(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
        }
        return repo.save(user);
    }

    @Override
    public User getUser(Integer id) {
        User user = repo.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", id));
        }
        return user;
    }

    @Override
    public void removeUser(Integer id) {
        repo.remove(id);
    }

    @Override
    public User updateUser(User user, Integer userId) {
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя равен null");
        }
        User foundUser = repo.get(userId);
        if (foundUser == null) {
            throw new NotFoundException(String.format("Пользователь для обновления с идентификатором %s не найден", userId));
        }
        if (isEmailDuplicate(user.getEmail()) && !user.getEmail().equals(foundUser.getEmail())) {
            throw new ValidationException(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
        }
        user.setId(userId);
        User updatedUser = fillUser(user, foundUser);
        return repo.update(updatedUser);
    }

    private boolean isEmailDuplicate(String email) {
        List<User> users = repo.getAll();
        return users.stream()
                .map(User::getEmail)
                .anyMatch(userEmail -> userEmail.equals(email));
    }

    private User fillUser(User newUser, User oldUser) {
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        } else if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        return newUser;
    }
}
