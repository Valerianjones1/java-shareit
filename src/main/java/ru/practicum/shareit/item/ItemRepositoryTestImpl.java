package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryTestImpl implements ItemRepositoryTest {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Map<Long, Item>> owners = new HashMap<>();
    private Long idCounter = 0L;


    @Override
    public Item create(Item item) {
        Long id = getId();
        item.setId(id);
        items.put(id, item);
        owners.computeIfAbsent(item.getOwner().getId(), k -> new HashMap<>())
                .put(id, item);

        return item;
    }

    @Override
    public Optional<Item> get(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAll(int ownerId) {
        return new ArrayList<>(owners.getOrDefault(ownerId, Collections.emptyMap())
                .values());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        owners.getOrDefault(item.getOwner().getId(), Collections.emptyMap())
                .put(item.getId(), item);
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

    private Long getId() {
        return ++idCounter;
    }
}
