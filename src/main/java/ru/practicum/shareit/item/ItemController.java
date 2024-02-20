package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                            @RequestBody Item item) {
        return service.saveItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId,
                              @RequestBody Item item) {
        return service.updateItem(item, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return service.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems() {
        return service.getAllItems();
    }
}
