package ru.practicum.shareit.booking;

import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repo;
    private final UserService userService;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    @Override
    public BookingResponseDto create(BookingDto bookingDto, long userId) {
        checkIfUserExists(userId);

        User booker = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));
        Item item = itemRepo.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", bookingDto.getItemId())));

        Booking booking = BookingMapper.mapToBooking(bookingDto, booker, item);

        if (!booking.getItem().getAvailable()) {
            throw new NotAvailableItemException("Запрещено бронировать недоступную вещь");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotRightBookerOrOwnerException("Владелец не может забронировать собственную вещь");
        }

        return BookingMapper.mapToBookingResponseDto(repo.save(booking));
    }

    @Override
    public BookingResponseDto updateStatus(long bookingId, long userId, Boolean approved) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new AlreadyApprovedException("Бронь уже подтверждена");
        }

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotRightBookerOrOwnerException("Подтвердить запрос на бронирование может только владелец вещи");
        }

        booking.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);

        return BookingMapper.mapToBookingResponseDto(repo.save(booking));
    }

    @Override
    public BookingResponseDto get(long bookingId, long userId) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (userId != bookerId && userId != ownerId) {
            throw new NotRightBookerOrOwnerException("Получить бронь можно либо владельцу вещи либо автором бронирования");
        }

        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByUser(long userId, String state) {
        checkIfStateSupports(state);
        checkIfUserExists(userId);

        return repo.findAllByBookerIdOrderByStartDateDesc(userId)
                .stream()
                .filter(booking -> getConditionByState(ParamState.valueOf(state), booking))
                .map(BookingMapper::mapToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwnerItems(long userId, String state) {
        checkIfStateSupports(state);
        checkIfUserExists(userId);

        return repo.findAllByItemOwnerIdOrderByStartDateDesc(userId)
                .stream()
                .filter(booking -> getConditionByState(ParamState.valueOf(state), booking))
                .map(BookingMapper::mapToBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(long userId) {
        userService.get(userId);
    }

    private void checkIfStateSupports(String state) {
        if (!Enums.getIfPresent(ParamState.class, state).isPresent()) {
            throw new NotSupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private boolean getConditionByState(ParamState state, Booking booking) {
        boolean condition;
        switch (state) {
            case PAST:
                condition = booking.getEndDate().isBefore(LocalDateTime.now());
                break;
            case CURRENT:
                condition = booking.getEndDate().isAfter(LocalDateTime.now())
                        && booking.getStartDate().isBefore(LocalDateTime.now());
                break;
            case REJECTED:
                condition = booking.getStatus().equals(BookingState.REJECTED);
                break;
            case WAITING:
                condition = booking.getStatus().equals(BookingState.WAITING);
                break;
            case FUTURE:
                condition = booking.getStartDate().isAfter(LocalDateTime.now())
                        && booking.getEndDate().isAfter(LocalDateTime.now());
                break;
            default:
                condition = booking != null;
                break;
        }
        return condition;
    }
}
