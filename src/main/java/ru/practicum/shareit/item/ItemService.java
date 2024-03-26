package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto get(long itemId);

    List<ItemDto> getAll(long ownerId);

    ItemDto update(ItemDto itemDto, long userId);

    List<ItemDto> search(String text);
}
