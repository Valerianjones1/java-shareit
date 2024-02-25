package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    @Override
    public List<User> getAll() {
        return repo.getAll();
    }

    @Override
    public User save(User user) {
        if (isEmailDuplicate(user.getEmail())) {
            throw new DataAlreadyExistsException(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
        }
        return repo.save(user);
    }

    @Override
    public User get(int id) {
        return repo.get(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", id)));
    }

    @Override
    public void remove(int id) {
        repo.remove(id);
    }

    @Override
    public User update(User user) {
        User foundUser = repo.get(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь для обновления с идентификатором %s не найден",
                                user.getId())));
        if (isEmailDuplicate(user.getEmail()) && !user.getEmail().equals(foundUser.getEmail())) {
            throw new ValidationException(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
        }
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
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        return newUser;
    }
}
