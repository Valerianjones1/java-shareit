package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return repo.save(user);
    }

    @Override
    public User getUser(Integer id) {
        return repo.get(id);
    }

    @Override
    public void removeUser(Integer id) {
        repo.remove(id);
    }

    @Override
    public User updateUser(User user) {
        return repo.update(user);
    }
}
