package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotEndedBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl mockItemService;

    private Pageable pageable = Pageable.unpaged();

    private User user;

    private Booking booking;

    private CommentDto comment;

    private ItemCreateDto itemCreateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.ru");
        user.setName("test name");

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setId(1L);
        itemCreateDto.setName("test name");
        itemCreateDto.setDescription("test desc");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(null);

        Item item = ItemMapper.mapToItem(itemCreateDto);

        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        comment = new CommentDto();
        comment.setId(1L);
        comment.setText("test text");
        comment.setAuthorName("test name");
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void shouldCreateItem() {
        long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        ;
        Item item = ItemMapper.mapToItem(itemCreateDto);

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto createdItem = mockItemService.create(itemCreateDto, userId);

        assertEquals(createdItem.getDescription(), itemCreateDto.getDescription());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotCreateItemWhenUserNotFound() {
        long userId = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemService.create(itemCreateDto, userId));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", userId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldGetItemByIdWhenUserNotOwner() {
        long userId = 2L;
        long itemId = 1L;

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(ItemMapper.mapToItem(itemCreateDto, user, null)));

        Mockito
                .when(commentRepository.findAllByItemId(itemId))
                .thenReturn(Collections.emptyList());

        ItemDto itemDto = mockItemService.get(itemId, userId);

        assertNotNull(itemDto);
        assertEquals(itemDto.getDescription(), itemCreateDto.getDescription());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotGetItemByIdWhenItemNotFound() {
        long userId = 1L;
        long itemId = 1L;

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemService.get(itemId, userId));

        assertEquals(String.format("Вещь с идентификатором %s не найдена", itemId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldGetAllItemsByOwnerIdWhenNoBookings() {
        long ownerId = 1L;

        Item item = ItemMapper.mapToItem(itemCreateDto);
        Mockito
                .when(itemRepository.findAllByOwnerId(ownerId, pageable))
                .thenReturn(List.of(item));
        Mockito
                .when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> items = mockItemService.getAll(ownerId,
                pageable);

        assertFalse(items.isEmpty());
        assertEquals(ItemMapper.mapToItemDto(item), items.get(0));

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldGetAllItemsByOwnerIdWhenHaveBookingsAndComments() {
        long ownerId = 1L;

        Item item = ItemMapper.mapToItem(itemCreateDto);
        Mockito
                .when(itemRepository.findAllByOwnerId(ownerId, pageable))
                .thenReturn(List.of(item));
        Mockito
                .when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(List.of(booking));

        Mockito
                .when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> items = mockItemService.getAll(ownerId, pageable);

        assertFalse(items.isEmpty());
        assertEquals(ItemMapper.mapToItemDto(item).getDescription(), items.get(0).getDescription());
        assertEquals(ItemMapper.mapToItemDto(item).getId(), items.get(0).getId());
        assertEquals(BookingMapper.mapToBookingItemDto(booking), items.get(0).getLastBooking());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldGetEmptyItemsByOwnerId() {
        long ownerId = 1L;

        Mockito
                .when(itemRepository.findAllByOwnerId(ownerId, pageable))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> items = mockItemService.getAll(ownerId, pageable);

        assertTrue(items.isEmpty());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldUpdateItem() {
        ItemCreateDto updatedCreateDto = itemCreateDto;
        updatedCreateDto.setName("updated Name");

        Item item = ItemMapper.mapToItem(itemCreateDto);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Item updatedItem = item;
        updatedItem.setName(updatedItem.getName());

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDto = mockItemService.update(updatedCreateDto, user.getId());

        assertEquals(ItemMapper.mapToItemDto(item), itemDto);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotUpdateItemWhenItemNotFound() {
        ItemCreateDto updatedCreateDto = itemCreateDto;
        updatedCreateDto.setName("updated Name");

        Item item = ItemMapper.mapToItem(itemCreateDto);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemService.update(updatedCreateDto, user.getId()));

        assertEquals(String.format("Вещь для обновления с идентификатором %s не найдена", updatedCreateDto.getId()),
                exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotUpdateItemWhenUSerNotFound() {
        ItemCreateDto updatedCreateDto = itemCreateDto;
        updatedCreateDto.setName("updated Name");

        Item item = ItemMapper.mapToItem(itemCreateDto);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockItemService.update(updatedCreateDto, user.getId()));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", user.getId()), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldSearchItems() {
        Item item = ItemMapper.mapToItem(itemCreateDto);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(),
                        anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));


        List<ItemDto> searchedItems = mockItemService.search("test name", Pageable.unpaged());

        assertFalse(searchedItems.isEmpty());
        assertEquals(ItemMapper.mapToItemDto(item), searchedItems.get(0));

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldReturnEmptySearchedItems() {
        List<ItemDto> searchedItems = mockItemService.search("", Pageable.unpaged());

        assertTrue(searchedItems.isEmpty());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldCreateComment() {
        Item item = ItemMapper.mapToItem(itemCreateDto);

        long userId = user.getId();
        long itemId = item.getId();

        booking.setBooker(user);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(anyLong(),
                        anyLong(), any(BookingStatus.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Comment createComment = CommentMapper.mapToComment(comment, user, item);

        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(createComment);

        CommentDto commentDto = mockItemService.createComment(itemId, comment, userId);

        assertEquals(CommentMapper.mapToCommentDto(createComment), commentDto);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotCreateCommentWhenBookingStatusNotApproved() {
        Item item = ItemMapper.mapToItem(itemCreateDto);

        long userId = user.getId();
        long itemId = item.getId();

        booking.setBooker(user);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(anyLong(),
                        anyLong(), any(BookingStatus.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.empty());

        NotEndedBookingException exception = assertThrows(
                NotEndedBookingException.class,
                () -> mockItemService.createComment(itemId, comment, userId));

        assertEquals("Срок брони должен быть окончен и статус 'APPROVED'", exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotCreateCommentWhenNotBooker() {
        Item item = ItemMapper.mapToItem(itemCreateDto);

        long userId = user.getId();
        long itemId = item.getId();

        booking.setBooker(user);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(anyLong(),
                        anyLong(), any(BookingStatus.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> mockItemService.createComment(itemId, comment, 2L));

        assertEquals("Оставлять комментарии может только человек, который бронировал", exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotCreateCommentWhenBookingNotEnded() {
        Item item = ItemMapper.mapToItem(itemCreateDto);

        long userId = user.getId();
        long itemId = item.getId();

        booking.setBooker(user);
        booking.setItem(item);

        booking.setEndDate(LocalDateTime.now().plusHours(4));

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(anyLong(),
                        anyLong(), any(BookingStatus.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.of(booking));

        NotEndedBookingException exception = assertThrows(NotEndedBookingException.class,
                () -> mockItemService.createComment(itemId, comment, userId));

        assertEquals("Комментировать можно только брони у которых срок закончен", exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void shouldNotCreateCommentWhenUserNotFound() {
        Item item = ItemMapper.mapToItem(itemCreateDto);

        long userId = user.getId();
        long itemId = item.getId();

        booking.setBooker(user);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(anyLong(),
                        anyLong(), any(BookingStatus.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> mockItemService.createComment(itemId, comment, userId));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", userId), exception.getMessage());

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository, commentRepository, bookingRepository);
    }


}
