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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@email.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setDescription("desc");
        item1.setAvailable(true);
        item1.setName("name");
        item1.setOwner(user);

        item2 = new Item();
        item2.setId(2L);
        item1.setDescription("test");
        item1.setAvailable(true);
        item1.setName("est");
        item1.setOwner(user);
    }

    @Test
    void shouldReturnSearchedItems() {
        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);

        String textToSearch = "test";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());


        assertEquals(1, items.size());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
    }

    @Test
    void shouldReturnEmptySearchedItems() {
        String textToSearch = "test";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());

        assertTrue(items.isEmpty());
        assertEquals(0, items.size());
    }
}
