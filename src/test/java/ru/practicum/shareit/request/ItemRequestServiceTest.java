package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl mockItemRequestService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.ru");
        user.setName("test name");
    }

    @Test
    void shouldCreateItemRequest() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("test description");
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto, user);

        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto createdItemRequest = mockItemRequestService.create(requestorId, itemRequestCreateDto);

        assertEquals(createdItemRequest.getDescription(), itemRequestCreateDto.getDescription());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotCreateItemRequestWhenUserIsNotFound() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.empty());

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("test description");

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemRequestService.create(requestorId, itemRequestCreateDto));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", requestorId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldGetAllItemRequestsByRequestorId() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("test description");
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto, user);

        Mockito
                .when(itemRequestRepository.findAllByRequestorId(requestorId, Sort.by("created").descending()))
                .thenReturn(List.of(itemRequest));

        Mockito
                .when(itemRepository.findAllByRequestIdIn(Stream.of(itemRequest)
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList())))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = mockItemRequestService.getAll(requestorId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        assertFalse(itemRequests.isEmpty());
        assertEquals(itemRequestDto, itemRequests.get(0));

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdWhenListEmpty() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepository.findAllByRequestorId(requestorId, Sort.by("created").descending()))
                .thenReturn(Collections.emptyList());

        Mockito
                .when(itemRepository.findAllByRequestIdIn(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = mockItemRequestService.getAll(requestorId);

        assertTrue(itemRequests.isEmpty());
        assertEquals(Collections.emptyList(), itemRequests);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdWhenUserIsNotFound() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemRequestService.getAll(requestorId));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", requestorId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldGetAllItemRequestsByRequestorIdNot() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("test description");
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto, user);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created").descending());

        Mockito
                .when(itemRequestRepository.findAllByRequestorIdNot(requestorId, pageable))
                .thenReturn(List.of(itemRequest));

        Mockito
                .when(itemRepository.findAllByRequestIdIn(Stream.of(itemRequest)
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList())))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = mockItemRequestService.getAll(requestorId, pageable);
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        assertFalse(itemRequests.isEmpty());
        assertEquals(itemRequestDto, itemRequests.get(0));

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdNotWhenListEmpty() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created").descending());
        Mockito
                .when(itemRequestRepository.findAllByRequestorIdNot(requestorId, pageable))
                .thenReturn(Collections.emptyList());

        Mockito
                .when(itemRepository.findAllByRequestIdIn(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = mockItemRequestService.getAll(requestorId, pageable);

        assertTrue(itemRequests.isEmpty());
        assertEquals(Collections.emptyList(), itemRequests);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdNotWhenUserIsNotFound() {
        long requestorId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemRequestService.getAll(requestorId,
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created").descending())));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", requestorId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }


    @Test
    void shouldGetItemRequestByRequestorId() {
        long requestorId = 1L;
        long requestId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("test description");
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto, user);

        Mockito
                .when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));

        Mockito
                .when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDto = mockItemRequestService.get(requestorId, requestId);

        assertNotNull(itemRequestDto);
        assertEquals(itemRequestCreateDto.getDescription(), itemRequestDto.getDescription());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetItemRequestByRequestorIdWhenItemRequestNotFound() {
        long requestorId = 1L;
        long requestId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemRequestService.get(requestorId, requestId));

        assertEquals(String.format("Запрос на вещь с идентификатором %s не найден", requestId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void shouldNotGetItemRequestByRequestorIdWhenUserIsNotFound() {
        long requestorId = 1L;

        long requestId = 1L;

        Mockito
                .when(userRepository.findById(requestorId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemRequestService.get(requestorId, requestId));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", requestorId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

}
