package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> get(int itemId);

    List<Item> getAll(int ownerId);

    Item update(Item item);

    List<Item> search(String text);
}
