package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Создаем вещь {}", itemCreateDto);
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemCreateDto itemCreateDto) {
        log.info(String.format("Обновляем вещь %s с идентификатором %s", itemCreateDto, itemId));
        itemCreateDto.setId(itemId);
        return itemClient.updateItem(userId, itemId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Получаем вещь с идентификатором %s", itemId));
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfOwner(@RequestHeader(CUSTOM_USER_ID_HEADER) long ownerId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info(String.format("Получаем вещь владельца с идентификатором %s", ownerId));

        return itemClient.getItemsOfOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info(String.format("Ищем вещи по любому полю, которое содержит %s в себе", text));
        return itemClient.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        log.info(String.format("Создаем комментарий к вещи с идентификатором %s", itemId));
        return itemClient.createComment(itemId, userId, commentDto);
    }
}