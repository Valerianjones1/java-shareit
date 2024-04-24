package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                              @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Создаем вещь {}", itemCreateDto);
        return service.create(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemCreateDto itemCreateDto) {
        log.info(String.format("Обновляем вещь %s с идентификатором %s", itemCreateDto, itemId));
        itemCreateDto.setId(itemId);
        return service.update(itemCreateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Получаем вещь с идентификатором %s", itemId));
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfOwner(@RequestHeader(CUSTOM_USER_ID_HEADER) long ownerId,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        log.info(String.format("Получаем вещь владельца с идентификатором %s", ownerId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return service.getAll(ownerId, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam int from,
                                     @RequestParam int size) {
        log.info(String.format("Ищем вещи по любому полю, которое содержит %s в себе", text));
        Pageable pageable = PageRequest.of(from / size, size);
        return service.search(text, pageable);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestBody CommentDto commentDto,
                                    @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Создаем комментарий к вещи с идентификатором %s", itemId));
        return service.createComment(itemId, commentDto, userId);
    }
}
