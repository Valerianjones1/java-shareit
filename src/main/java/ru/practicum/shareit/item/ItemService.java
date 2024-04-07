package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemCreateDto itemCreateDto, long userId);

    ItemDto get(long itemId, long userId);

    List<ItemDto> getAll(long ownerId);

    ItemDto update(ItemCreateDto itemCreateDto, long userId);

    List<ItemDto> search(String text);

    CommentDto createComment(Long itemId, CommentDto commentDto, long userId);
}
