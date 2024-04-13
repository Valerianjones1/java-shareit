package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository repository;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setEmail("test@email.ru");
        user.setName("test name");

        user = repository.save(user);

        List<User> users = repository.findAll();
        User foundUser = repository.findById(user.getId()).orElse(null);

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
        assertEquals(user, foundUser);
    }
}
