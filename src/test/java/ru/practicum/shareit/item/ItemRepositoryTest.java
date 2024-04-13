package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("test");
        user.setEmail("test@email.ru");

        item1 = new Item();
        item1.setDescription("testdesc");
        item1.setAvailable(true);
        item1.setName("name");
        item1.setOwner(user);

        item2 = new Item();
        item2.setDescription("test");
        item2.setAvailable(true);
        item2.setName("est");
        item2.setOwner(user);

        item3 = new Item();
        item3.setDescription("desc");
        item3.setAvailable(true);
        item3.setName("name");
        item3.setOwner(user);
    }

    @Test
    void shouldReturnSearchedItems() {
        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        String textToSearch = "test";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());


        assertEquals(2, items.size());
    }

    @Test
    void shouldReturnEmptySearchedItems() {
        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        String textToSearch = "no";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());

        assertTrue(items.isEmpty());
        assertEquals(0, items.size());
    }
}
