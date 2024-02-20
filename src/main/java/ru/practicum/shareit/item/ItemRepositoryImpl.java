package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Integer, Item> items = new HashMap<>();

    private Integer idCounter = 0;


    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.put(getId(), item);
        return item;
    }

    @Override
    public Item get(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item update(Item item) {
        items.put(getId(), item);
        return item;
    }

    private Integer getId() {
        return ++idCounter;
    }

}
