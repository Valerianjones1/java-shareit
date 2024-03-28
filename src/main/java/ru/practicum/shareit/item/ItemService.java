package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemResponseDto get(long itemId, long userId);

    List<ItemResponseDto> getAll(long ownerId);

    ItemDto update(ItemDto itemDto, long userId);

    List<ItemDto> search(String text);
}
