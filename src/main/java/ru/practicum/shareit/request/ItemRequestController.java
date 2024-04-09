package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId,
                                            @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
        return service.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId) {
        return service.getAll(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId, @PathVariable long requestId) {
        return service.get(requestorId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        Pageable pageable = from == null || size == null ?
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created").descending().descending()) :
                PageRequest.of(from / size,  size, Sort.by("created").descending());
        checkParams(size, from);
        return service.getAll(requestorId, pageable);
    }

    private void checkParams(Integer size, Integer from) {
        if ((from != null || size != null) && (from < 0 || size < 0)) {
            throw new ValidationException("Параметры size и from не могут меньше нуля");
        }
    }
}
