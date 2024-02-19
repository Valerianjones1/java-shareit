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
            throw new ValidationException("Нельзя создавать пользователей с одинаковыми почтами");
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
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Идентификатор пользователя равен null");
        }
        if (repo.get(user.getId()) == null) {
            throw new NotFoundException(String.format("Пользователь для обновления с идентификатором %s не найден", user.getId()));
        }
        return repo.update(user);
    }

    private boolean isEmailDuplicate(String email) {
        List<User> users = repo.getAll();
        return users.stream()
                .map(User::getEmail)
                .anyMatch(userEmail -> userEmail.equals(email));
    }
}
