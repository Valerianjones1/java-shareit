package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryTest {
    Item create(Item item);

    Optional<Item> get(int itemId);

    List<Item> getAll(int ownerId);

    Item update(Item item);

    List<Item> search(String text);
}
