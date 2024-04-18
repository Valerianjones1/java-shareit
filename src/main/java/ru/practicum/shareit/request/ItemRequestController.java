package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
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
                                                   @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return service.getAll(requestorId, pageable);
    }
}
