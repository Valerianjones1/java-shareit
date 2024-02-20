package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item get(Integer itemId);

    List<Item> getAll();

    Item update(Item item);
}
