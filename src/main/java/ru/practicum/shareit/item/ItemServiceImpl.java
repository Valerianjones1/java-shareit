package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotRightOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;

    private final UserService userService;

    private final ModelMapper mapper;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        checkIfUserExists(userId);
        Item item = mapper.map(itemDto, Item.class);

        User owner = mapper.map(userService.get(userId), User.class);
        item.setOwner(owner);

        Item savedItem = repo.save(item);
        return mapper.map(savedItem, ItemDto.class);
    }

    @Override
    public ItemDto get(long itemId) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", itemId)));
        return mapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getAll(long ownerId) {
        List<Item> items = repo.findAllByOwnerId(ownerId);
        return items.stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        Item oldItem = repo.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь для обновления с идентификатором %s не найдена", itemDto.getId())));

        User user = mapper.map(userService.get(userId), User.class);
        if (!oldItem.getOwner().equals(user)) {
            throw new NotRightOwnerException("Вещь может редактировать только владелец");
        }

        Item item = mapper.map(itemDto, Item.class);
        Item updatedItem = fillItem(item, oldItem);

        repo.save(updatedItem);
        return mapper.map(updatedItem, ItemDto.class);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> searchedItems = repo.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text);
        return searchedItems.stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(long userId) {
        userService.get(userId);
    }

    private Item fillItem(Item newItem, Item oldItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        if (newItem.getOwner() != null) {
            oldItem.setOwner(newItem.getOwner());
        }
        return oldItem;
    }
}
