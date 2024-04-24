package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId,
                                                    @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
        return itemRequestClient.createRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId) {
        return itemRequestClient.getRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequest(requestorId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(CUSTOM_USER_ID_HEADER) long requestorId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemRequestClient.getAllItemRequests(requestorId, from, size);
    }
}
