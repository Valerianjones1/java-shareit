package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Integer userId);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getAllItems(Integer ownerId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId );
}
