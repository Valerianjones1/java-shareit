package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.AlreadyApprovedException;
import ru.practicum.shareit.exception.NotAvailableItemException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;

    private BookingCreateDto bookingCreateDto;
    private Booking booking;

    private Booking futureBooking;
    private Booking pastBooking;
    private Booking currentBooking;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.ru");
        user.setName("test name");

        item = new Item();
        item.setId(1L);
        item.setName("test name");
        item.setDescription("test desc");
        item.setAvailable(true);
        item.setOwner(user);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateDto.setItemId(item.getId());

        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        currentBooking = new Booking();
        currentBooking.setId(2L);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setStartDate(LocalDateTime.now().minusDays(1));
        currentBooking.setEndDate(LocalDateTime.now().plusHours(2));
        currentBooking.setBooker(user);
        currentBooking.setItem(item);


        pastBooking = new Booking();
        pastBooking.setId(3L);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(2));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        pastBooking.setBooker(user);
        pastBooking.setItem(item);

        futureBooking = new Booking();
        futureBooking.setId(4L);
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setStartDate(LocalDateTime.now().plusHours(1));
        futureBooking.setEndDate(LocalDateTime.now().plusHours(2));
        futureBooking.setBooker(user);
        futureBooking.setItem(item);
    }

    @Test
    void shouldCreateBooking() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(bookingCreateDto.getItemId()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(repository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.create(bookingCreateDto, 2L);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void shouldNotCreateBookingWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.create(bookingCreateDto, user.getId()));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", user.getId()),
                exception.getMessage());
    }

    @Test
    void shouldNotCreateBookingWhenItemNotFound() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(bookingCreateDto.getItemId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingCreateDto, user.getId()));

        assertEquals(String.format("Вещь с идентификатором %s не найдена", bookingCreateDto.getItemId()),
                exception.getMessage());
    }

    @Test
    void shouldNotCreateBookingWhenItemNotAvailable() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        item.setAvailable(false);

        Mockito
                .when(itemRepository.findById(bookingCreateDto.getItemId()))
                .thenReturn(Optional.of(item));

        NotAvailableItemException exception = assertThrows(NotAvailableItemException.class,
                () -> bookingService.create(bookingCreateDto, user.getId()));

        assertEquals("Запрещено бронировать недоступную вещь",
                exception.getMessage());
    }

    @Test
    void shouldNotCreateBookingWhenOwnerIsBooker() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(bookingCreateDto.getItemId()))
                .thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingCreateDto, 1L));

        assertEquals("Владелец не может забронировать собственную вещь", exception.getMessage());
    }

    @Test
    void shouldUpdateStatusOfBooking() {
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(repository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.updateStatus(booking.getId(), user.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void shouldNotUpdateStatusOfBookingWhenAlreadyApproved() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        AlreadyApprovedException exception = assertThrows(AlreadyApprovedException.class,
                () -> bookingService.updateStatus(booking.getId(), user.getId(), true));

        assertEquals("Бронь уже подтверждена", exception.getMessage());
    }

    @Test
    void shouldNotUpdateStatusOfBookingWhenUserNotOwner() {
        booking.setStatus(BookingStatus.WAITING);
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        long notOwnerId = 2L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(booking.getId(), notOwnerId, true));

        assertEquals("Подтвердить запрос на бронирование может только владелец вещи",
                exception.getMessage());
    }

    @Test
    void shouldGetBooking() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.get(booking.getId(), user.getId());

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void shouldNotGetBookingWhenNotOwnerOrBooker() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.get(booking.getId(), 2L));

        assertEquals("Получить бронь можно либо владельцу вещи либо автором бронирования",
                exception.getMessage());
    }

    @Test
    void shouldNotGetBookingWhenBookingNotFound() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.get(booking.getId(), 2L));

        assertEquals(String.format("Бронь с идентификатором %s не найдена", booking.getId()),
                exception.getMessage());
    }

    @Test
    void shouldGetAllBookingsWithBookingStateAll() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking, pastBooking, currentBooking, futureBooking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.ALL, Pageable.unpaged());

        assertEquals(4, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
    }

    @Test
    void shouldGetAllBookingsWithBookingStateRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.REJECTED);

        Mockito
                .when(repository.findAllByBookerIdAndStatusEquals(anyLong(), any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.REJECTED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
        assertEquals(BookingStatus.REJECTED, booking.getStatus());

    }

    @Test
    void shouldGetAllBookingsWithBookingStateWaiting() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(repository.findAllByBookerIdAndStatusEquals(anyLong(), any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.WAITING, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void shouldGetAllBookingsWithBookingStateCurrent() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByBookerIdWithCurrentState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(currentBooking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.CURRENT, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(currentBooking), bookings.get(0));
        assertTrue(currentBooking.getStartDate().isBefore(LocalDateTime.now()));
        assertTrue(currentBooking.getEndDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldGetAllBookingsWithBookingStatePast() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByBookerIdWithPastState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(pastBooking, booking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.PAST, Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(pastBooking), bookings.get(0));
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(1));
        assertTrue(pastBooking.getEndDate().isBefore(LocalDateTime.now()));
    }

    @Test
    void shouldGetAllBookingsWithBookingStateFuture() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByBookerIdWithFutureState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(futureBooking));

        List<BookingDto> bookings = bookingService.getAllByUser(user.getId(), BookingState.FUTURE, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(futureBooking), bookings.get(0));
        assertTrue(futureBooking.getEndDate().isAfter(LocalDateTime.now()));
        assertTrue(futureBooking.getStartDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldNotGetAllBookingsWhenUserNotExists() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.getAllByUser(user.getId(), BookingState.FUTURE, Pageable.unpaged()));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", user.getId()), exception.getMessage());
    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStateAll() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking, pastBooking, currentBooking, futureBooking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(),
                BookingState.ALL, Pageable.unpaged());

        assertEquals(4, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStateRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.REJECTED);

        Mockito
                .when(repository.findAllByItemOwnerIdAndStatusEquals(anyLong(), any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(), BookingState.REJECTED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
        assertEquals(BookingStatus.REJECTED, booking.getStatus());

    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStateWaiting() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(repository.findAllByItemOwnerIdAndStatusEquals(anyLong(), any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(), BookingState.WAITING, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(0));
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStateCurrent() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByItemOwnerIdWithCurrentState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(currentBooking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(), BookingState.CURRENT, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(currentBooking), bookings.get(0));
        assertTrue(currentBooking.getStartDate().isBefore(LocalDateTime.now()));
        assertTrue(currentBooking.getEndDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStatePast() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByItemOwnerIdWithPastState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(pastBooking, booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(), BookingState.PAST, Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(pastBooking), bookings.get(0));
        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.get(1));
        assertTrue(pastBooking.getEndDate().isBefore(LocalDateTime.now()));
    }

    @Test
    void shouldGetAllBookingsByOwnerWithBookingStateFuture() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findAllByItemOwnerIdWithFutureState(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(futureBooking));

        List<BookingDto> bookings = bookingService.getAllByOwnerItems(user.getId(), BookingState.FUTURE, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(BookingMapper.mapToBookingDto(futureBooking), bookings.get(0));
        assertTrue(futureBooking.getEndDate().isAfter(LocalDateTime.now()));
        assertTrue(futureBooking.getStartDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldNotGetAllBookingsByOwnerWhenUserNotExists() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.getAllByOwnerItems(user.getId(), BookingState.FUTURE, Pageable.unpaged()));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", user.getId()), exception.getMessage());
    }
}
