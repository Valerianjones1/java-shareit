package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    public ItemDto createItem(@RequestHeader(CUSTOM_USER_ID_HEADER) int userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return service.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CUSTOM_USER_ID_HEADER) int userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return service.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable Long itemId,
                                   @RequestHeader(CUSTOM_USER_ID_HEADER) int userId) {
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllItemsOfOwner(@RequestHeader(CUSTOM_USER_ID_HEADER) int ownerId) {
        return service.getAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return service.search(text);
    }
}
