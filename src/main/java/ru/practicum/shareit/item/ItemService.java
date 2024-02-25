package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, int userId);

    ItemDto get(int itemId);

    List<ItemDto> getAll(int ownerId);

    ItemDto update(ItemDto itemDto, int userId);

    List<ItemDto> search(String text);
}
