package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;

    private final ModelMapper mapper;

    @Override
    public ItemDto saveItem(Item item, Integer userId) {
        item.setOwner(userId);
        Item savedItem = repo.save(item);
        return mapper.map(savedItem, ItemDto.class);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = repo.get(itemId);
        return mapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> items = repo.getAll();
        return items.stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Item item, Integer itemId) {
        item.setId(itemId);
        Item updatedItem = repo.update(item);
        return mapper.map(updatedItem, ItemDto.class);
    }
}
