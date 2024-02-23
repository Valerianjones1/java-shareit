package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotRightOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;

    private final UserService userService;

    private final ModelMapper mapper;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Integer userId) {
        if (!isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        Item item = mapper.map(itemDto, Item.class);
        item.setOwner(userId);
        Item savedItem = repo.save(item);
        return mapper.map(savedItem, ItemDto.class);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = repo.get(itemId);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с идентификатором %s не найдена", itemId));
        }
        return mapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItems(Integer ownerId) {
        List<Item> items = repo.getAll(ownerId);
        return items.stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item oldItem = repo.get(itemId);
        if (oldItem == null) {
            throw new NotFoundException(String.format("Вещь для обновления с идентификатором %s не найдена", itemId));
        }
        if (!oldItem.getOwner().equals(userId)) {
            throw new NotRightOwnerException("Вещь может редактировать только владелец");
        }
        Item item = mapper.map(itemDto, Item.class);
        item.setId(itemId);
        Item updatedItem = repo.update(fillItem(item, oldItem));
        return mapper.map(updatedItem, ItemDto.class);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> searchedItems = repo.searchItems(text);
        return searchedItems.stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    private boolean isUserExists(Integer userId) {
        try {
            userService.getUser(userId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private Item fillItem(Item newItem, Item oldItem) {
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        if (newItem.getOwner() == null) {
            newItem.setOwner(oldItem.getOwner());
        }
        if (newItem.getRequest() == null) {
            newItem.setRequest(oldItem.getRequest());
        }
        return newItem;
    }
}
