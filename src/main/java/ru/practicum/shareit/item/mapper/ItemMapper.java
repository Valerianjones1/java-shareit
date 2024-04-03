package ru.practicum.shareit.item.mapper;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());

        return itemDto;
    }

    public static ItemDto mapToItemDto(Item item, BookingItemDto lastBooking,
                                       BookingItemDto nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);

        itemDto.setComments(comments);

        return itemDto;
    }

    public static Item mapToItem(ItemCreateDto itemCreateDto, User owner) {
        Item item = new Item();
        item.setId(itemCreateDto.getId());
        item.setName(itemCreateDto.getName());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setDescription(itemCreateDto.getDescription());
        item.setOwner(owner);

        return item;
    }

    public static Item mapToItem(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setId(itemCreateDto.getId());
        item.setName(itemCreateDto.getName());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setDescription(itemCreateDto.getDescription());

        return item;
    }
}
