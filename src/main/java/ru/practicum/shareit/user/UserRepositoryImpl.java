package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Integer id) {
        return users.get(id);
    }

    @Override
    public void remove(Integer id) {
        users.remove(id);
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            return null;
        }
        users.put(user.getId(), user);
        return user;
    }
}
