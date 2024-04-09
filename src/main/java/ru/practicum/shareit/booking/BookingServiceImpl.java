package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.AlreadyApprovedException;
import ru.practicum.shareit.exception.NotAvailableItemException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(BookingCreateDto bookingCreateDto, long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", bookingCreateDto.getItemId())));

        Booking booking = BookingMapper.mapToBooking(bookingCreateDto, booker, item);

        if (!booking.getItem().getAvailable()) {
            throw new NotAvailableItemException("Запрещено бронировать недоступную вещь");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        return BookingMapper.mapToBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto updateStatus(long bookingId, long userId, Boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyApprovedException("Бронь уже подтверждена");
        }

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Подтвердить запрос на бронирование может только владелец вещи");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto get(long bookingId, long userId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (userId != bookerId && userId != ownerId) {
            throw new NotFoundException("Получить бронь можно либо владельцу вещи либо автором бронирования");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, BookingState state, Pageable pageable) {
        checkIfUserExists(userId);
        List<Booking> bookers;
        switch (state) {
            case PAST:
                bookers = repository.findAllByBookerIdWithPastState(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookers = repository.findAllByBookerIdWithFutureState(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookers = repository.findAllByBookerIdWithCurrentState(userId, LocalDateTime.now(), pageable);
                break;
            case REJECTED:
                bookers = repository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookers = repository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                bookers = repository.findAllByBookerId(userId, pageable);
                break;
        }

        return bookers.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwnerItems(long userId, BookingState state, Pageable pageable) {
        checkIfUserExists(userId);
        List<Booking> bookers;
        switch (state) {
            case PAST:
                bookers = repository.findAllByItemOwnerIdWithPastState(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookers = repository.findAllByItemOwnerIdWithFutureState(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookers = repository.findAllByItemOwnerIdWithCurrentState(userId, LocalDateTime.now(), pageable);
                break;
            case REJECTED:
                bookers = repository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookers = repository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                bookers = repository.findAllByItemOwnerId(userId, pageable);
                break;
        }

        return bookers.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));
    }
}
