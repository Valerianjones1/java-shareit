package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("test@email.ru");
        user1.setName("test name");

        user2 = new User();
        user2.setEmail("test2@email.ru");
        user2.setName("test2 name");

        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("test1 request");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequestor(user1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("test2 request");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequestor(user2);

        itemRequest3 = new ItemRequest();
        itemRequest3.setDescription("test3 request");
        itemRequest3.setCreated(LocalDateTime.now());
        itemRequest3.setRequestor(user1);
    }

    @Test
    void shouldFindAllByRequestorId() {
        userRepository.save(user1);
        userRepository.save(user2);

        repository.save(itemRequest1);
        repository.save(itemRequest2);
        repository.save(itemRequest3);

        List<ItemRequest> itemRequests = repository.findAllByRequestorId(user1.getId(), Sort.by("id").ascending());

        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest1, itemRequests.get(0));
    }

    @Test
    void shouldReturnEmptyFindAllByRequestorId() {
        userRepository.save(user1);
        List<ItemRequest> itemRequests = repository.findAllByRequestorId(user1.getId(), Sort.by("id").ascending());

        assertTrue(itemRequests.isEmpty());
        assertEquals(0, itemRequests.size());
    }

    @Test
    void shouldFindAllByRequestorIdNot() {
        userRepository.save(user1);
        userRepository.save(user2);

        repository.save(itemRequest1);
        repository.save(itemRequest2);
        repository.save(itemRequest3);

        List<ItemRequest> itemRequests = repository.findAllByRequestorIdNot(user1.getId(), Pageable.unpaged());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest2, itemRequests.get(0));
    }

    @Test
    void shouldReturnEmptyFindAllByRequestorIdNot() {
        userRepository.save(user1);

        List<ItemRequest> itemRequests = repository.findAllByRequestorId(user1.getId(), Sort.by("id").ascending());

        assertTrue(itemRequests.isEmpty());
        assertEquals(0, itemRequests.size());
    }
}
