package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto create(long requestorId, ItemRequestCreateDto itemRequestDto) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", requestorId)));

        ItemRequest itemRequest = repository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto, requestor));

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAll(long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", requestorId)));


        List<ItemRequest> requests = repository.findAllByRequestorId(requestorId, Sort.by("created").descending());

        return getItemRequestDtos(requests);
    }

    @Override
    public ItemRequestDto get(long requestorId, long requestId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", requestorId)));

        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос на вещь с идентификатором %s не найден", requestId)));

        List<ItemDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAll(long requestorId, Pageable pageable) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", requestorId)));

        List<ItemRequest> requests = repository.findAllByRequestorIdNot(requestorId, pageable);

        return getItemRequestDtos(requests);
    }

    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());


        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsReq = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemsDto = itemsReq.getOrDefault(request.getId(), new ArrayList<>())
                            .stream()
                            .map(ItemMapper::mapToItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.mapToItemRequestDto(request, itemsDto);
                })
                .collect(Collectors.toList());
    }
}
