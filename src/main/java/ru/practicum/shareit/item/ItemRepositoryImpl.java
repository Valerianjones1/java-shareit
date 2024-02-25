package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private Integer idCounter = 0;


    @Override
    public Item save(Item item) {
        Integer id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> get(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAll(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return items.values()
                .stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    private Integer getId() {
        return ++idCounter;
    }

}
