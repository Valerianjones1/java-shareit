package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataAlreadyExistsException;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Integer idCounter = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        checkIfEmailExists(user.getEmail());
        user.setId(getId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void remove(int id) {
        User userToDelete = users.get(id);
        if (userToDelete != null) {
            emails.remove(userToDelete.getEmail());
        }
        users.remove(id);
    }

    @Override
    public User update(User user, String oldEmail) {
        checkIfEmailExists(user.getEmail(), oldEmail);
        if (!oldEmail.equals(user.getEmail())) {
            emails.remove(oldEmail);
            emails.add(user.getEmail());
        }
        users.put(user.getId(), user);

        return user;
    }

    private Integer getId() {
        return ++idCounter;
    }


    private void checkIfEmailExists(String email) {
        if (emails.contains(email)) {
            throw new DataAlreadyExistsException(String.format("Пользователь с почтой %s уже зарегистрирован", email));
        }
    }

    private void checkIfEmailExists(String email, String oldEmail) {
        if (emails.contains(email) && !oldEmail.equals(email)) {
            throw new DataAlreadyExistsException(String.format("Пользователь с почтой %s уже зарегистрирован", email));
        }
    }
}
