package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Item get(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll(Integer ownerId) {
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
    public List<Item> searchItems(String text) {
        return text.isBlank() ? Collections.emptyList() :
                items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Integer getId() {
        return ++idCounter;
    }

}
