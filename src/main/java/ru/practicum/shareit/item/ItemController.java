package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto saveItem(@RequestHeader(CUSTOM_USER_ID_HEADER) Integer userId,
                            @Valid @RequestBody ItemDto itemDto) {
        return service.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CUSTOM_USER_ID_HEADER) Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return service.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfOwner(@RequestHeader(CUSTOM_USER_ID_HEADER) Integer ownerId) {
        return service.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return service.searchItems(text);
    }
}
