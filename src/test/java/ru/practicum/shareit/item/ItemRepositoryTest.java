package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("test");
        owner.setEmail("test@email.ru");

        booker = new User();
        booker.setName("testBook");
        booker.setEmail("testBook@email.ru");

        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("test1 request");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequestor(booker);

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("test2 request");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequestor(booker);


        item1 = new Item();
        item1.setDescription("testdesc");
        item1.setAvailable(true);
        item1.setName("name");
        item1.setOwner(owner);
        item1.setRequest(itemRequest1);

        item2 = new Item();
        item2.setDescription("test");
        item2.setAvailable(true);
        item2.setName("est");
        item2.setOwner(owner);
        item2.setRequest(itemRequest2);

        item3 = new Item();
        item3.setDescription("desc");
        item3.setAvailable(true);
        item3.setName("name");
        item3.setOwner(booker);

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void shouldReturnSearchedItems() {
        String textToSearch = "test";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());

        assertEquals(2, items.size());
    }

    @Test
    void shouldReturnEmptySearchedItems() {
        String textToSearch = "no";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                textToSearch, textToSearch, Pageable.unpaged());

        assertTrue(items.isEmpty());
        assertEquals(0, items.size());
    }

    @Test
    void shouldFindAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId(), Pageable.unpaged());

        assertEquals(2, items.size());
    }

    @Test
    void shouldReturnEmptyFindAllByOwnerIdWhenUserNotFound() {
        List<Item> items = itemRepository.findAllByOwnerId(4L, Pageable.unpaged());

        assertEquals(0, items.size());
    }

    @Test
    void shouldReturnEmptyFindAllByOwnerIdWhenNoItems() {
        itemRepository.delete(item1);
        itemRepository.delete(item2);
        itemRepository.delete(item3);

        List<Item> items = itemRepository.findAllByOwnerId(owner.getId(), Pageable.unpaged());

        assertEquals(0, items.size());
    }
}
