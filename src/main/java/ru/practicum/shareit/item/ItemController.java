package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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
                              @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Создаем вещь " + itemCreateDto);
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
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {
        log.info(String.format("Получаем вещь владельца с идентификатором %s", ownerId));

        Pageable pageable = from == null || size == null ?
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending()) :
                PageRequest.of(from / size, size, Sort.by("id").ascending());
        checkParams(size, from);
        return service.getAll(ownerId, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {
        log.info(String.format("Ищем вещи по любому полю, которое содержит %s в себе", text));
        Pageable pageable = from == null || size == null ? Pageable.unpaged() : PageRequest.of(from / size, size);
        checkParams(size, from);
        return service.search(text, pageable);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Создаем комментарий к вещи с идентификатором %s", itemId));
        return service.createComment(itemId, commentDto, userId);
    }

    private void checkParams(Integer size, Integer from) {
        if ((from != null || size != null) && (from < 0 || size < 0)) {
            throw new ValidationException("Параметры size и from не могут меньше нуля");
        }
    }
}
